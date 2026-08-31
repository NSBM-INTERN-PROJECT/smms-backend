package com.smms.meeting_service.service;

import com.smms.meeting_service.domain.*;
import com.smms.meeting_service.dto.request.*;
import com.smms.meeting_service.dto.response.*;
import com.smms.meeting_service.exception.MeetingNotFoundException;
import com.smms.meeting_service.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class MeetingService {

    private final MeetingRepository meetingRepo;
    private final NotificationService notificationService;

    // ─── Queries ────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MeetingResponse> getTodaysMeetings(Long mentorUserId) {
        return meetingRepo.findByMentorUserIdAndScheduledDate(mentorUserId, LocalDate.now())
                .stream().map(MeetingResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MeetingResponse> getUpcomingForMentor(Long mentorUserId) {
        return meetingRepo.findByMentorUserIdAndStatusAndScheduledDateAfter(
                mentorUserId, MeetingStatus.SCHEDULED, LocalDate.now().minusDays(1))
                .stream().map(MeetingResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MeetingResponse> getUpcomingForStudent(Long studentUserId) {
        return meetingRepo.findByStudentUserIdAndStatusAndScheduledDateAfter(
                studentUserId, MeetingStatus.SCHEDULED, LocalDate.now().minusDays(1))
                .stream().map(MeetingResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagedResponse<MeetingResponse> getMentorHistory(Long mentorUserId, int page, int size) {
        return PagedResponse.from(
                meetingRepo.findByMentorUserIdOrderByScheduledDateDesc(
                        mentorUserId, PageRequest.of(page, size, Sort.by("scheduledDate").descending())),
                MeetingResponse::from);
    }

    @Transactional(readOnly = true)
    public PagedResponse<MeetingResponse> getStudentHistory(Long studentUserId, int page, int size) {
        return PagedResponse.from(
                meetingRepo.findByStudentUserIdOrderByScheduledDateDesc(
                        studentUserId, PageRequest.of(page, size, Sort.by("scheduledDate").descending())),
                MeetingResponse::from);
    }

    // ─── Mutations ─────────────────────────────────────────────────────────────────

    /** Mark a meeting's attendance (mentor action). */
    @Transactional
    public MeetingResponse markAttendance(Long meetingId, Long mentorUserId, AttendanceRequest req) {
        Meeting meeting = getAndValidateMentor(meetingId, mentorUserId);
        if (meeting.getStatus() != MeetingStatus.SCHEDULED && meeting.getStatus() != MeetingStatus.COMPLETED)
            throw new IllegalStateException("Attendance can only be marked for SCHEDULED or COMPLETED meetings.");
        meeting.setAttendanceStatus(req.getAttendanceStatus());
        if (req.getAttendanceStatus() != AttendanceStatus.PENDING)
            meeting.setStatus(MeetingStatus.COMPLETED);
        return MeetingResponse.from(meetingRepo.save(meeting));
    }

    /** Reschedule a meeting — creates a new meeting record linked to the original. */
    @Transactional
    public MeetingResponse reschedule(Long meetingId, Long mentorUserId, RescheduleMeetingRequest req) {
        Meeting old = getAndValidateMentor(meetingId, mentorUserId);
        if (old.getStatus() != MeetingStatus.SCHEDULED)
            throw new IllegalStateException("Only SCHEDULED meetings can be rescheduled.");

        // Mark old as RESCHEDULED
        old.setStatus(MeetingStatus.RESCHEDULED);
        meetingRepo.save(old);

        // Create new meeting
        Meeting newMeeting = Meeting.builder()
                .allocationId(old.getAllocationId())
                .mentorUserId(old.getMentorUserId())
                .studentUserId(old.getStudentUserId())
                .title(old.getTitle())
                .description(old.getDescription())
                .scheduledDate(req.getNewDate())
                .scheduledTime(req.getNewTime())
                .durationMinutes(old.getDurationMinutes())
                .location(old.getLocation())
                .mode(old.getMode())
                .meetingLink(old.getMeetingLink())
                .status(MeetingStatus.SCHEDULED)
                .rescheduledFromId(old.getId())
                .rescheduleCount(old.getRescheduleCount() + 1)
                .build();

        Meeting saved = meetingRepo.save(newMeeting);

        notificationService.push(old.getStudentUserId(), NotificationType.MEETING_RESCHEDULED,
                "Meeting Rescheduled",
                "Your meeting has been rescheduled to " + req.getNewDate() + " at " + req.getNewTime() +
                (req.getReason() != null ? ". Reason: " + req.getReason() : "."),
                saved.getId(), "Meeting");

        return MeetingResponse.from(saved);
    }

    /** Cancel a meeting. */
    @Transactional
    public MeetingResponse cancel(Long meetingId, Long mentorUserId, CancelRequest req) {
        Meeting meeting = getAndValidateMentor(meetingId, mentorUserId);
        if (meeting.getStatus() != MeetingStatus.SCHEDULED)
            throw new IllegalStateException("Only SCHEDULED meetings can be cancelled.");
        meeting.setStatus(MeetingStatus.CANCELLED);
        meeting.setCancelledReason(req.getReason());
        meetingRepo.save(meeting);

        notificationService.push(meeting.getStudentUserId(), NotificationType.MEETING_CANCELLED,
                "Meeting Cancelled",
                "Your meeting scheduled for " + meeting.getScheduledDate() + " has been cancelled." +
                (req.getReason() != null ? " Reason: " + req.getReason() : ""),
                meetingId, "Meeting");

        return MeetingResponse.from(meeting);
    }

    /** Mark a meeting as COMPLETED (without attendance detail — quick complete). */
    @Transactional
    public MeetingResponse complete(Long meetingId, Long mentorUserId) {
        Meeting meeting = getAndValidateMentor(meetingId, mentorUserId);
        if (meeting.getStatus() != MeetingStatus.SCHEDULED)
            throw new IllegalStateException("Only SCHEDULED meetings can be marked complete.");
        meeting.setStatus(MeetingStatus.COMPLETED);
        if (meeting.getAttendanceStatus() == AttendanceStatus.PENDING)
            meeting.setAttendanceStatus(AttendanceStatus.PRESENT);
        return MeetingResponse.from(meetingRepo.save(meeting));
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────────

    private Meeting getAndValidateMentor(Long meetingId, Long mentorUserId) {
        Meeting meeting = meetingRepo.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException(meetingId));
        if (!meeting.getMentorUserId().equals(mentorUserId))
            throw new com.smms.meeting_service.exception.MeetingException("ACCESS_DENIED",
                    "Meeting does not belong to you", org.springframework.http.HttpStatus.FORBIDDEN);
        return meeting;
    }
}
