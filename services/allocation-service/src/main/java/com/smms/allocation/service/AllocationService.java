package com.smms.allocation.service;

import com.smms.allocation.client.UserServiceClient;
import com.smms.allocation.client.dto.MentorCapacityDto;
import com.smms.allocation.client.dto.StudentSummaryDto;
import com.smms.allocation.domain.Allocation;
import com.smms.allocation.domain.AllocationStatus;
import com.smms.allocation.domain.AllocationType;
import com.smms.allocation.dto.request.DeactivateRequest;
import com.smms.allocation.dto.request.ManualAllocationRequest;
import com.smms.allocation.dto.request.RandomAllocationRequest;
import com.smms.allocation.dto.request.TransferRequest;
import com.smms.allocation.dto.response.AllocationResponse;
import com.smms.allocation.dto.response.PagedResponse;
import com.smms.allocation.dto.response.RandomAllocationResult;
import com.smms.allocation.exception.*;
import com.smms.allocation.repository.AllocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AllocationService {

    private final AllocationRepository allocationRepo;
    private final UserServiceClient userServiceClient;

    // ─── Manual Allocation ────────────────────────────────────────────────────

    /**
     * Admin/Coordinator manually assigns a student to a mentor.
     * Guards: student must not have an active allocation, mentor must have capacity.
     */
    @Transactional
    public AllocationResponse manualAllocate(ManualAllocationRequest req, Long coordinatorUserId) {
        // Guard 1: student must not already be allocated
        if (allocationRepo.existsByStudentUserIdAndStatus(req.getStudentUserId(), AllocationStatus.ACTIVE)) {
            throw new StudentAlreadyAllocatedException(req.getStudentUserId());
        }

        // Guard 2: mentor capacity check (we trust user-service for maxStudents;
        // for simplicity we allow up to a default of 10 if Feign call fails)
        long currentCount = allocationRepo.countByMentorUserIdAndStatus(
                req.getMentorUserId(), AllocationStatus.ACTIVE);
        int maxStudents = getMentorMaxStudents(req.getMentorUserId());
        if (currentCount >= maxStudents) {
            throw new MentorAtCapacityException(req.getMentorUserId(), maxStudents);
        }

        Allocation allocation = Allocation.builder()
                .mentorUserId(req.getMentorUserId())
                .studentUserId(req.getStudentUserId())
                .coordinatorUserId(coordinatorUserId)
                .allocationType(AllocationType.MANUAL)
                .status(AllocationStatus.ACTIVE)
                .allocatedDate(LocalDate.now())
                .notes(req.getNotes())
                .build();

        return AllocationResponse.from(allocationRepo.save(allocation));
    }

    // ─── Random Allocation Engine ─────────────────────────────────────────────

    /**
     * Core algorithm: randomly distributes unallocated students across mentors
     * that still have capacity.
     *
     * Algorithm:
     * 1. Fetch all active students from user-service (optionally filtered by batch/dept)
     * 2. Subtract students who already have an ACTIVE allocation
     * 3. Fetch all active mentors from user-service with their maxStudents
     * 4. Build a capacity map: mentorId -> remaining slots
     * 5. Shuffle the unallocated students list (Fisher-Yates via Collections.shuffle)
     * 6. For each student, pick a random mentor that still has slots
     * 7. Decrement the mentor's remaining slots after each assignment
     * 8. If no mentors have slots, record as skipped
     * 9. Persist all successful allocations in one batch save
     */
    @Transactional
    public RandomAllocationResult randomAllocate(RandomAllocationRequest req, Long coordinatorUserId) {
        // Step 1: Fetch students from user-service
        List<StudentSummaryDto> allStudents;
        try {
            allStudents = userServiceClient.getActiveStudents(req.getBatch(), req.getDepartment());
        } catch (Exception e) {
            log.error("Failed to fetch students from user-service: {}", e.getMessage());
            throw new IllegalStateException("Cannot connect to user-service to fetch student list");
        }

        // Step 2: Subtract already-allocated students
        Set<Long> allocatedIds = new HashSet<>(allocationRepo.findAllocatedStudentIds());
        List<StudentSummaryDto> unallocated = allStudents.stream()
                .filter(s -> !allocatedIds.contains(s.getUserId()))
                .collect(Collectors.toList());

        if (unallocated.isEmpty()) {
            throw new NoUnallocatedStudentsException();
        }

        // Step 3: Fetch mentors and their capacities
        List<MentorCapacityDto> mentors;
        try {
            mentors = userServiceClient.getActiveMentorsWithCapacity();
        } catch (Exception e) {
            log.error("Failed to fetch mentors from user-service: {}", e.getMessage());
            throw new IllegalStateException("Cannot connect to user-service to fetch mentor list");
        }

        // Step 4: Build remaining capacity map (mentorId -> remaining slots)
        // Subtract already-assigned students from each mentor's capacity
        Map<Long, Long> currentCounts = allocationRepo.countActiveStudentsPerMentor().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]));

        // Filter to mentors with remaining capacity
        Map<Long, Integer> capacityMap = new LinkedHashMap<>();
        for (MentorCapacityDto mentor : mentors) {
            long assigned = currentCounts.getOrDefault(mentor.getUserId(), 0L);
            int remaining = mentor.getMaxStudents() - (int) assigned;
            if (remaining > 0 || !req.isSkipFullMentors()) {
                capacityMap.put(mentor.getUserId(), Math.max(0, remaining));
            }
        }

        if (capacityMap.isEmpty()) {
            throw new NoAvailableMentorsException();
        }

        // Step 5: Shuffle students for fairness
        List<StudentSummaryDto> shuffled = new ArrayList<>(unallocated);
        Collections.shuffle(shuffled);

        // Step 6–8: Allocate
        List<Allocation> toSave = new ArrayList<>();
        List<String> skippedReasons = new ArrayList<>();
        int skipped = 0;

        // Mutable capacity map for tracking slots during this run
        Map<Long, Integer> remaining = new HashMap<>(capacityMap);
        List<Long> mentorIds = new ArrayList<>(remaining.keySet());

        for (StudentSummaryDto student : shuffled) {
            // Find all mentors that still have capacity
            List<Long> available = mentorIds.stream()
                    .filter(mid -> remaining.getOrDefault(mid, 0) > 0)
                    .collect(Collectors.toList());

            if (available.isEmpty()) {
                skipped++;
                skippedReasons.add("Student " + student.getUserId() +
                        " skipped: no mentors with available capacity");
                continue;
            }

            // Pick a random available mentor
            Long selectedMentorId = available.get(new Random().nextInt(available.size()));

            toSave.add(Allocation.builder()
                    .mentorUserId(selectedMentorId)
                    .studentUserId(student.getUserId())
                    .coordinatorUserId(coordinatorUserId)
                    .allocationType(AllocationType.RANDOM)
                    .status(AllocationStatus.ACTIVE)
                    .allocatedDate(LocalDate.now())
                    .build());

            // Step 7: Decrement remaining capacity
            remaining.put(selectedMentorId, remaining.get(selectedMentorId) - 1);
        }

        // Step 9: Batch save
        List<Allocation> saved = allocationRepo.saveAll(toSave);
        log.info("Random allocation complete: {} allocated, {} skipped", saved.size(), skipped);

        return RandomAllocationResult.builder()
                .totalProcessed(shuffled.size())
                .successCount(saved.size())
                .skippedCount(skipped)
                .skippedReasons(skippedReasons)
                .allocations(saved.stream().map(AllocationResponse::from).collect(Collectors.toList()))
                .build();
    }

    // ─── Query Operations ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PagedResponse<AllocationResponse> listAll(int page, int size, AllocationStatus status) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var result = (status != null)
                ? allocationRepo.findByStatus(status, pageable)
                : allocationRepo.findAll(pageable);
        return PagedResponse.from(result, AllocationResponse::from);
    }

    @Transactional(readOnly = true)
    public List<AllocationResponse> getMentorStudents(Long mentorUserId) {
        return allocationRepo.findByMentorUserIdAndStatus(mentorUserId, AllocationStatus.ACTIVE)
                .stream().map(AllocationResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AllocationResponse getStudentMentor(Long studentUserId) {
        return allocationRepo.findByStudentUserIdAndStatus(studentUserId, AllocationStatus.ACTIVE)
                .map(AllocationResponse::from)
                .orElseThrow(() -> new AllocationNotFoundException(studentUserId));
    }

    @Transactional(readOnly = true)
    public List<Long> getUnallocatedStudentIds() {
        // Fetch all active student IDs from user-service, then subtract allocated ones
        try {
            List<StudentSummaryDto> all = userServiceClient.getActiveStudents(null, null);
            Set<Long> allocated = new HashSet<>(allocationRepo.findAllocatedStudentIds());
            return all.stream()
                    .map(StudentSummaryDto::getUserId)
                    .filter(id -> !allocated.contains(id))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("user-service unavailable: {}", e.getMessage());
            throw new IllegalStateException("Cannot fetch student list from user-service");
        }
    }

    // ─── Mutation Operations ──────────────────────────────────────────────────

    /**
     * Transfers a student from their current mentor to a new one.
     * Marks the old allocation TRANSFERRED and creates a new ACTIVE one.
     */
    @Transactional
    public AllocationResponse transfer(Long allocationId, TransferRequest req, Long coordinatorUserId) {
        Allocation old = allocationRepo.findById(allocationId)
                .orElseThrow(() -> new AllocationNotFoundException(allocationId));

        if (old.getStatus() != AllocationStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE allocations can be transferred.");
        }

        // Check new mentor capacity
        long currentCount = allocationRepo.countByMentorUserIdAndStatus(
                req.getNewMentorUserId(), AllocationStatus.ACTIVE);
        int maxStudents = getMentorMaxStudents(req.getNewMentorUserId());
        if (currentCount >= maxStudents) {
            throw new MentorAtCapacityException(req.getNewMentorUserId(), maxStudents);
        }

        // Mark old as TRANSFERRED
        old.setStatus(AllocationStatus.TRANSFERRED);
        old.setDeactivatedDate(LocalDate.now());
        old.setNotes((old.getNotes() != null ? old.getNotes() + " | " : "") +
                "Transferred to mentor " + req.getNewMentorUserId() +
                (req.getNotes() != null ? ": " + req.getNotes() : ""));
        allocationRepo.save(old);

        // Create new ACTIVE allocation
        Allocation newAlloc = Allocation.builder()
                .mentorUserId(req.getNewMentorUserId())
                .studentUserId(old.getStudentUserId())
                .coordinatorUserId(coordinatorUserId)
                .allocationType(AllocationType.MANUAL)
                .status(AllocationStatus.ACTIVE)
                .allocatedDate(LocalDate.now())
                .notes(req.getNotes())
                .build();

        return AllocationResponse.from(allocationRepo.save(newAlloc));
    }

    /** Deactivates an allocation (student leaves, internship ends, etc.) */
    @Transactional
    public AllocationResponse deactivate(Long allocationId, DeactivateRequest req) {
        Allocation allocation = allocationRepo.findById(allocationId)
                .orElseThrow(() -> new AllocationNotFoundException(allocationId));

        if (allocation.getStatus() != AllocationStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE allocations can be deactivated.");
        }

        allocation.setStatus(AllocationStatus.INACTIVE);
        allocation.setDeactivatedDate(LocalDate.now());
        if (req.getNotes() != null) {
            allocation.setNotes((allocation.getNotes() != null ?
                    allocation.getNotes() + " | " : "") + req.getNotes());
        }

        return AllocationResponse.from(allocationRepo.save(allocation));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Attempts to fetch a mentor's maxStudents from user-service.
     * Falls back to a sensible default of 5 if the service is unavailable.
     */
    private int getMentorMaxStudents(Long mentorUserId) {
        try {
            return userServiceClient.getActiveMentorsWithCapacity().stream()
                    .filter(m -> m.getUserId().equals(mentorUserId))
                    .map(MentorCapacityDto::getMaxStudents)
                    .findFirst()
                    .orElse(5);
        } catch (Exception e) {
            log.warn("Could not fetch mentor capacity for {}, defaulting to 5", mentorUserId);
            return 5;
        }
    }
}
