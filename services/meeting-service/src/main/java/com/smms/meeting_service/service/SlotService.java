package com.smms.meeting_service.service;

import com.smms.meeting_service.domain.*;
import com.smms.meeting_service.dto.request.*;
import com.smms.meeting_service.dto.response.*;
import com.smms.meeting_service.exception.SlotNotFoundException;
import com.smms.meeting_service.repository.MeetingRepository;
import com.smms.meeting_service.repository.MeetingSlotAllocationRepository;
import com.smms.meeting_service.repository.MeetingSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service 
@RequiredArgsConstructor 
@Slf4j
public class SlotService {

    private final MeetingSlotRepository slotRepo;
    private final MeetingSlotAllocationRepository allocationRepo;
    private final MeetingRepository meetingRepo;
    private final NotificationService notificationService;

    /**
     * Bulk-create slots for a mentor. Optionally auto-assigns each slot to a student
     * from the provided studentUserIds list (1-to-1 pairing by order).
     */
    @Transactional
    public BulkSlotResult bulkCreate(Long mentorUserId, BulkCreateSlotRequest req) {
        // Create all slots
        List<MeetingSlot> slots = req.getSlots().stream().map(s ->
                MeetingSlot.builder()
                        .mentorUserId(mentorUserId)
                        .slotDate(s.getSlotDate())
                        .startTime(s.getStartTime())
                        .durationMinutes(s.getDurationMinutes())
                        .mode(s.getMode())
                        .location(s.getLocation())
                        .meetingLink(s.getMeetingLink())
                        .filterCriteria(s.getFilterCriteria())
                        .status(SlotStatus.OPEN)
                        .build()
        ).collect(Collectors.toList());

        List<MeetingSlot> saved = slotRepo.saveAll(slots);
        List<MeetingSlotAllocation> allocations = new ArrayList<>();

        // Auto-assign students if provided
        List<Long> studentIds = req.getStudentUserIds();
        if (studentIds != null && !studentIds.isEmpty()) {
            int pairs = Math.min(saved.size(), studentIds.size());
            for (int i = 0; i < pairs; i++) {
                MeetingSlot slot = saved.get(i);
                Long studentId = studentIds.get(i);

                slot.setStatus(SlotStatus.ALLOCATED);
                slotRepo.save(slot);

                MeetingSlotAllocation alloc = MeetingSlotAllocation.builder()
                        .slotId(slot.getId())
                        .studentUserId(studentId)
                        .status(SlotAllocationStatus.PENDING)
                        .build();
                allocations.add(allocationRepo.save(alloc));

                // Notify student
                notificationService.push(studentId, NotificationType.SLOT_ASSIGNED,
                        "Meeting Slot Assigned",
                        "Your mentor has assigned you a meeting slot on " + slot.getSlotDate() +
                        " at " + slot.getStartTime() + ". Please respond.",
                        slot.getId(), "MeetingSlot");
            }
        }

        return BulkSlotResult.builder()
                .totalRequested(req.getSlots().size())
                .createdCount(saved.size())
                .assignedCount(allocations.size())
                .slots(saved.stream().map(SlotResponse::from).collect(Collectors.toList()))
                .allocations(allocations.stream().map(SlotAllocationResponse::from).collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public PagedResponse<SlotResponse> getMentorSlots(Long mentorUserId, int page, int size) {
        return PagedResponse.from(
                slotRepo.findByMentorUserIdOrderBySlotDateDescStartTimeDesc(
                        mentorUserId, PageRequest.of(page, size)),
                SlotResponse::from);
    }

    @Transactional
    public SlotResponse cancelSlot(Long slotId, Long mentorUserId) {
        MeetingSlot slot = slotRepo.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException(slotId));
        if (!slot.getMentorUserId().equals(mentorUserId))
            throw new com.smms.meeting_service.exception.MeetingException("ACCESS_DENIED",
                    "Slot does not belong to you", org.springframework.http.HttpStatus.FORBIDDEN);
        slot.setStatus(SlotStatus.CANCELLED);
        // Notify affected students
        allocationRepo.findBySlotId(slotId).forEach(a ->
                notificationService.push(a.getStudentUserId(), NotificationType.MEETING_CANCELLED,
                        "Meeting Slot Cancelled", "A meeting slot scheduled for " +
                        slot.getSlotDate() + " has been cancelled by your mentor.",
                        slotId, "MeetingSlot"));
        return SlotResponse.from(slotRepo.save(slot));
    }

    @Transactional(readOnly = true)
    public List<SlotResponse> getStudentSlotInvitations(Long studentUserId) {
        List<MeetingSlotAllocation> pendingAllocations = allocationRepo
                .findByStudentUserIdAndStatus(studentUserId, SlotAllocationStatus.PENDING);
        List<SlotResponse> result = new ArrayList<>();
        for (MeetingSlotAllocation alloc : pendingAllocations) {
            slotRepo.findById(alloc.getSlotId()).ifPresent(slot -> {
                result.add(SlotResponse.from(slot, alloc));
            });
        }
        return result;
    }

    /** Student responds to a slot assignment: ACCEPTED or RESCHEDULE_REQUESTED. */
    @Transactional
    public SlotAllocationResponse respondToSlot(Long idOrSlotId, Long studentUserId,
                                                 SlotResponseRequest req) {
        MeetingSlotAllocation alloc = allocationRepo.findById(idOrSlotId)
                .or(() -> allocationRepo.findBySlotIdAndStudentUserId(idOrSlotId, studentUserId))
                .orElseThrow(() -> new com.smms.meeting_service.exception.MeetingException(
                        "ALLOCATION_NOT_FOUND", "Slot allocation not found: " + idOrSlotId,
                        org.springframework.http.HttpStatus.NOT_FOUND));

        if (!alloc.getStudentUserId().equals(studentUserId))
            throw new com.smms.meeting_service.exception.MeetingException("ACCESS_DENIED",
                    "Not your slot allocation", org.springframework.http.HttpStatus.FORBIDDEN);

        alloc.setStudentResponse(req.getResponse());
        alloc.setRespondedAt(LocalDateTime.now());

        MeetingSlot slot = slotRepo.findById(alloc.getSlotId()).orElse(null);

        if (req.getResponse() == StudentResponse.ACCEPTED) {
            alloc.setStatus(SlotAllocationStatus.CONFIRMED);
            if (slot != null) {
                Meeting meeting = Meeting.builder()
                        .mentorUserId(slot.getMentorUserId())
                        .studentUserId(studentUserId)
                        .title("Mentoring Consultation Session")
                        .description("Slot scheduled session")
                        .scheduledDate(slot.getSlotDate())
                        .scheduledTime(slot.getStartTime())
                        .durationMinutes(slot.getDurationMinutes() != null ? slot.getDurationMinutes() : 30)
                        .location(slot.getLocation())
                        .mode(slot.getMode() != null ? slot.getMode() : MeetingMode.PHYSICAL)
                        .meetingLink(slot.getMeetingLink())
                        .status(MeetingStatus.SCHEDULED)
                        .attendanceStatus(AttendanceStatus.PENDING)
                        .build();
                meetingRepo.save(meeting);
            }
        } else {
            alloc.setStatus(SlotAllocationStatus.RESCHEDULE_REQUESTED);
            alloc.setRescheduleReason(req.getRescheduleReason());
        }

        allocationRepo.save(alloc);

        // Get slot to notify mentor
        if (slot != null) {
            notificationService.push(slot.getMentorUserId(), NotificationType.SLOT_RESPONSE,
                    "Student Responded to Slot",
                    "Student " + studentUserId + " has " +
                    (req.getResponse() == StudentResponse.ACCEPTED ? "accepted" : "requested a reschedule for") +
                    " the slot on " + slot.getSlotDate() + ".",
                    alloc.getId(), "MeetingSlotAllocation");
        }

        return SlotAllocationResponse.from(alloc);
    }
}
