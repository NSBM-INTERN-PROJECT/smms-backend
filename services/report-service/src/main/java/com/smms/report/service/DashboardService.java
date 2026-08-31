package com.smms.report.service;

import com.smms.report.client.AllocationServiceClient;
import com.smms.report.client.MeetingServiceClient;
import com.smms.report.client.SessionServiceClient;
import com.smms.report.client.UserServiceClient;
import com.smms.report.client.dto.*;
import com.smms.report.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class DashboardService {

    private final UserServiceClient userClient;
    private final AllocationServiceClient allocationClient;
    private final MeetingServiceClient meetingClient;
    private final SessionServiceClient sessionClient;

    /**
     * Admin/Coordinator dashboard — aggregates KPIs across all mentors/students.
     */
    public DashboardStats getAdminDashboard(Long adminUserId, String role) {
        // --- Students & Mentors ---
        List<StudentSummaryDto> students = userClient.getActiveStudents(null, null);
        List<MentorSummaryDto> mentors  = userClient.getActiveMentors();

        // --- Allocations ---
        PagedResponseDto<AllocationDto> allocPage =
                allocationClient.listAll(0, 1000, null);
        List<AllocationDto> allocs = allocPage.getContent();
        long allocated   = allocs.stream().filter(a -> "ACTIVE".equals(a.getStatus())).count();
        long unallocated = userClient.getActiveStudents(null, null).stream().count() - allocated;

        // --- Meetings ---
        long totalMeetings = 0, completed = 0, scheduled = 0, cancelled = 0;
        long present = 0, absent = 0, late = 0;

        // Aggregate meetings across all mentors
        for (MentorSummaryDto mentor : mentors) {
            try {
                PagedResponseDto<MeetingDto> meetings = meetingClient.getMentorMeetingHistory(
                        mentor.getUserId(), "ADMIN", 0, 500);
                if (meetings.getContent() != null) {
                    for (MeetingDto m : meetings.getContent()) {
                        totalMeetings++;
                        switch (m.getStatus()) {
                            case "COMPLETED"  -> completed++;
                            case "SCHEDULED"  -> scheduled++;
                            case "CANCELLED"  -> cancelled++;
                        }
                        if (m.getAttendanceStatus() != null) {
                            switch (m.getAttendanceStatus()) {
                                case "PRESENT" -> present++;
                                case "ABSENT"  -> absent++;
                                case "LATE"    -> late++;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to fetch meetings for mentor {}: {}", mentor.getUserId(), e.getMessage());
            }
        }

        // --- Escalations ---
        PagedResponseDto<EscalationDto> escPage =
                sessionClient.listAllEscalations(adminUserId, role, 0, 1000, null, null);
        List<EscalationDto> escalations = escPage != null && escPage.getContent() != null
                ? escPage.getContent() : List.of();
        long openEsc     = escalations.stream().filter(e -> "OPEN".equals(e.getStatus())).count();
        long resolvedEsc = escalations.stream().filter(e -> "RESOLVED".equals(e.getStatus())
                || "CLOSED".equals(e.getStatus())).count();

        // --- Progress ---
        long onTrack = 0, needsAtt = 0, atRisk = 0, critical = 0;
        for (MentorSummaryDto mentor : mentors) {
            try {
                List<StudentProgressSummaryDto> summaries =
                        sessionClient.getMentorProgressSummary(mentor.getUserId(), "ADMIN");
                if (summaries != null) {
                    for (StudentProgressSummaryDto s : summaries) {
                        if (s.getLatestProgressStatus() == null) continue;
                        switch (s.getLatestProgressStatus()) {
                            case "ON_TRACK"        -> onTrack++;
                            case "NEEDS_ATTENTION" -> needsAtt++;
                            case "AT_RISK"         -> atRisk++;
                            case "CRITICAL"        -> critical++;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to fetch progress for mentor {}: {}", mentor.getUserId(), e.getMessage());
            }
        }

        return DashboardStats.builder()
                .totalStudents(students.size())
                .allocatedStudents(allocated)
                .unallocatedStudents(Math.max(0, unallocated))
                .totalMentors(mentors.size())
                .totalAllocations(allocs.size())
                .totalMeetings(totalMeetings)
                .completedMeetings(completed)
                .scheduledMeetings(scheduled)
                .cancelledMeetings(cancelled)
                .presentCount(present)
                .absentCount(absent)
                .lateCount(late)
                .studentsOnTrack(onTrack)
                .studentsNeedsAttention(needsAtt)
                .studentsAtRisk(atRisk)
                .studentsCritical(critical)
                .openEscalations(openEsc)
                .resolvedEscalations(resolvedEsc)
                .totalEscalations(escalations.size())
                .build();
    }

    /**
     * Mentor dashboard — their own students, meeting counts, progress status breakdown.
     */
    public MentorDashboard getMentorDashboard(Long mentorUserId) {
        // Progress summaries
        List<StudentProgressSummaryDto> summaries;
        try {
            summaries = sessionClient.getMentorProgressSummary(mentorUserId, "MENTOR");
        } catch (Exception e) {
            log.warn("Could not fetch progress summary for mentor {}: {}", mentorUserId, e.getMessage());
            summaries = List.of();
        }

        // Meeting history
        PagedResponseDto<MeetingDto> meetingPage;
        try {
            meetingPage = meetingClient.getMentorMeetingHistory(mentorUserId, "MENTOR", 0, 500);
        } catch (Exception e) {
            log.warn("Could not fetch meetings for mentor {}: {}", mentorUserId, e.getMessage());
            meetingPage = new PagedResponseDto<>();
        }
        List<MeetingDto> meetings = meetingPage.getContent() != null
                ? meetingPage.getContent() : List.of();

        long completed = meetings.stream().filter(m -> "COMPLETED".equals(m.getStatus())).count();
        long openEsc   = summaries.stream().mapToLong(s -> s.getOpenEscalations() != null
                ? s.getOpenEscalations() : 0).sum();

        int onTrack = 0, needsAtt = 0, atRisk = 0, critical = 0;
        for (StudentProgressSummaryDto s : summaries) {
            if (s.getLatestProgressStatus() == null) { onTrack++; continue; }
            switch (s.getLatestProgressStatus()) {
                case "ON_TRACK"        -> onTrack++;
                case "NEEDS_ATTENTION" -> needsAtt++;
                case "AT_RISK"         -> atRisk++;
                case "CRITICAL"        -> critical++;
            }
        }

        return MentorDashboard.builder()
                .mentorUserId(mentorUserId)
                .totalStudents(summaries.size())
                .studentsOnTrack(onTrack)
                .studentsNeedsAttention(needsAtt)
                .studentsAtRisk(atRisk)
                .studentsCritical(critical)
                .totalMeetings(meetings.size())
                .completedMeetings(completed)
                .openEscalations(openEsc)
                .studentSummaries(summaries)
                .build();
    }

    /**
     * Student dashboard — their own meeting and progress stats.
     */
    public StudentDashboard getStudentDashboard(Long studentUserId) {
        // Meetings
        PagedResponseDto<MeetingDto> meetingPage;
        try {
            meetingPage = meetingClient.getStudentMeetingHistory(studentUserId, "STUDENT", 0, 500);
        } catch (Exception e) {
            log.warn("Could not fetch meetings for student {}: {}", studentUserId, e.getMessage());
            meetingPage = new PagedResponseDto<>();
        }
        List<MeetingDto> meetings = meetingPage.getContent() != null
                ? meetingPage.getContent() : List.of();

        long completed = meetings.stream().filter(m -> "COMPLETED".equals(m.getStatus())).count();
        long upcoming  = meetings.stream().filter(m -> "SCHEDULED".equals(m.getStatus())).count();
        long present   = meetings.stream().filter(m -> "PRESENT".equals(m.getAttendanceStatus())).count();
        long absent    = meetings.stream().filter(m -> "ABSENT".equals(m.getAttendanceStatus())).count();

        // Session notes
        PagedResponseDto<SessionNoteDto> notePage;
        try {
            notePage = sessionClient.getStudentNotes(studentUserId, studentUserId, "STUDENT", 0, 500);
        } catch (Exception e) {
            log.warn("Could not fetch session notes for student {}: {}", studentUserId, e.getMessage());
            notePage = new PagedResponseDto<>();
        }
        List<SessionNoteDto> notes = notePage.getContent() != null ? notePage.getContent() : List.of();
        String latestStatus = notes.isEmpty() ? "ON_TRACK" : notes.get(0).getProgressStatus();

        // Escalations
        PagedResponseDto<EscalationDto> escPage;
        try {
            escPage = sessionClient.getStudentEscalations(studentUserId, studentUserId, "STUDENT", 0, 100);
        } catch (Exception e) {
            log.warn("Could not fetch escalations for student {}: {}", studentUserId, e.getMessage());
            escPage = new PagedResponseDto<>();
        }
        long openEsc = escPage.getContent() != null
                ? escPage.getContent().stream().filter(e -> "OPEN".equals(e.getStatus())).count() : 0;

        // Allocation → get mentorUserId
        Long mentorId = null;
        try {
            AllocationDto alloc = allocationClient.getMentorAllocations(0L).stream()
                    .filter(a -> studentUserId.equals(a.getStudentUserId()) && "ACTIVE".equals(a.getStatus()))
                    .findFirst().orElse(null);
            if (alloc != null) mentorId = alloc.getMentorUserId();
        } catch (Exception ignored) {}

        return StudentDashboard.builder()
                .studentUserId(studentUserId)
                .mentorUserId(mentorId)
                .latestProgressStatus(latestStatus)
                .totalMeetings(meetings.size())
                .completedMeetings(completed)
                .upcomingMeetings(upcoming)
                .attendancePresent(present)
                .attendanceAbsent(absent)
                .openEscalations(openEsc)
                .totalSessionNotes(notes.size())
                .build();
    }

    /**
     * FR-005: Advanced mentor-student filtering view for coordinators.
     */
    public List<MentorStudentView> getMentorStudentView(MentorFilterRequest filter) {
        List<MentorSummaryDto> mentors = userClient.getActiveMentors();
        List<StudentSummaryDto> allStudents = userClient.getActiveStudents(
                filter.getBatch(), filter.getDepartment());

        return mentors.stream()
                // Dept filter
                .filter(m -> filter.getDepartment() == null
                        || filter.getDepartment().equalsIgnoreCase(m.getDepartment()))
                // Specialization filter
                .filter(m -> filter.getSpecialization() == null
                        || filter.getSpecialization().equalsIgnoreCase(m.getSpecialization()))
                .map(mentor -> {
                    // Get allocated students for this mentor
                    List<AllocationDto> mentorAllocs;
                    try {
                        mentorAllocs = allocationClient.getMentorAllocations(mentor.getUserId());
                    } catch (Exception e) {
                        mentorAllocs = List.of();
                    }

                    List<Long> studentIds = mentorAllocs.stream()
                            .filter(a -> "ACTIVE".equals(a.getStatus()))
                            .map(AllocationDto::getStudentUserId)
                            .collect(Collectors.toList());

                    List<StudentSummaryDto> mentorStudents = allStudents.stream()
                            .filter(s -> studentIds.contains(s.getUserId()))
                            .collect(Collectors.toList());

                    // Progress summaries
                    List<StudentProgressSummaryDto> summaries;
                    try {
                        summaries = sessionClient.getMentorProgressSummary(mentor.getUserId(), "ADMIN");
                    } catch (Exception e) {
                        summaries = List.of();
                    }

                    // Apply progress status filter
                    if (filter.getProgressStatus() != null) {
                        boolean hasMatchingStudent = summaries.stream()
                                .anyMatch(s -> filter.getProgressStatus()
                                        .equalsIgnoreCase(s.getLatestProgressStatus()));
                        if (!hasMatchingStudent) return null;
                    }

                    // Apply student count filter
                    if (filter.getMinStudents() != null && mentorStudents.size() < filter.getMinStudents())
                        return null;
                    if (filter.getMaxStudents() != null && mentorStudents.size() > filter.getMaxStudents())
                        return null;

                    return MentorStudentView.builder()
                            .mentorUserId(mentor.getUserId())
                            .mentorName(mentor.getFullName())
                            .department(mentor.getDepartment())
                            .specialization(mentor.getSpecialization())
                            .capacity(mentor.getMaxStudents())
                            .currentStudentCount(mentorStudents.size())
                            .students(mentorStudents)
                            .progressSummaries(summaries)
                            .build();
                })
                .filter(v -> v != null)
                .collect(Collectors.toList());
    }
}
