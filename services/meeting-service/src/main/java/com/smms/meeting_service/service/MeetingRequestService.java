package com.smms.meeting_service.service;

import com.smms.meeting_service.domain.*;
import com.smms.meeting_service.dto.request.*;
import com.smms.meeting_service.dto.response.*;
import com.smms.meeting_service.exception.RequestNotFoundException;
import com.smms.meeting_service.repository.MeetingRepository;
import com.smms.meeting_service.repository.MeetingRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service @RequiredArgsConstructor @Slf4j
public class MeetingRequestService {

    private final MeetingRequestRepository requestRepo;
    private final MeetingRepository meetingRepository;
    private final NotificationService notificationService;

    /** Student submits a meeting request to their mentor. */
    @Transactional
    public MeetingRequestResponse submit(Long studentUserId, MeetingRequestDto req) {
        MeetingRequest request = MeetingRequest.builder()
                .studentUserId(studentUserId)
                .mentorUserId(req.getMentorUserId())
                .preferredDate(req.getPreferredDate())
                .preferredTime(req.getPreferredTime())
                .reason(req.getReason())
                .status(MeetingRequestStatus.PENDING)
                .build();

        request = requestRepo.save(request);

        notificationService.push(req.getMentorUserId(), NotificationType.MEETING_REQUEST,
                "New Meeting Request",
                "Student " + studentUserId + " has requested a meeting on " +
                req.getPreferredDate() + " at " + req.getPreferredTime() + ".",
                request.getId(), "MeetingRequest");

        return MeetingRequestResponse.from(request);
    }

    /** Mentor views pending requests from their students. */
    @Transactional(readOnly = true)
    public PagedResponse<MeetingRequestResponse> getPendingForMentor(Long mentorUserId, int page, int size) {
        return PagedResponse.from(
                requestRepo.findByMentorUserIdAndStatusOrderByCreatedAtAsc(
                        mentorUserId, MeetingRequestStatus.PENDING, PageRequest.of(page, size)),
                MeetingRequestResponse::from);
    }

    /** Student views their own request history. */
    @Transactional(readOnly = true)
    public PagedResponse<MeetingRequestResponse> getStudentHistory(Long studentUserId, int page, int size) {
        return PagedResponse.from(
                requestRepo.findByStudentUserIdOrderByCreatedAtDesc(
                        studentUserId, PageRequest.of(page, size)),
                MeetingRequestResponse::from);
    }

    /** Mentor approves a request — creates a SCHEDULED meeting record. */
    @Transactional
    public MeetingResponse approve(Long requestId, Long mentorUserId, Long allocationId,
                                    ReviewMeetingRequest review) {
        MeetingRequest req = getAndValidate(requestId, mentorUserId);
        req.setStatus(MeetingRequestStatus.APPROVED);
        req.setMentorResponseNotes(review.getMentorResponseNotes());
        req.setRespondedAt(LocalDateTime.now());
        requestRepo.save(req);

        // Create the meeting
        Meeting meeting = Meeting.builder()
                .allocationId(allocationId)
                .mentorUserId(mentorUserId)
                .studentUserId(req.getStudentUserId())
                .title("Meeting with student " + req.getStudentUserId())
                .scheduledDate(req.getPreferredDate())
                .scheduledTime(req.getPreferredTime())
                .status(MeetingStatus.SCHEDULED)
                .build();

        Meeting saved = meetingRepository.save(meeting);

        notificationService.push(req.getStudentUserId(), NotificationType.MEETING_SCHEDULED,
                "Meeting Request Approved",
                "Your meeting request has been approved. Meeting scheduled for " +
                req.getPreferredDate() + " at " + req.getPreferredTime() + ".",
                saved.getId(), "Meeting");

        return MeetingResponse.from(saved);
    }

    /** Mentor rejects a request. */
    @Transactional
    public MeetingRequestResponse reject(Long requestId, Long mentorUserId, ReviewMeetingRequest review) {
        MeetingRequest req = getAndValidate(requestId, mentorUserId);
        req.setStatus(MeetingRequestStatus.REJECTED);
        req.setMentorResponseNotes(review.getMentorResponseNotes());
        req.setRespondedAt(LocalDateTime.now());
        requestRepo.save(req);

        notificationService.push(req.getStudentUserId(), NotificationType.MEETING_SCHEDULED,
                "Meeting Request Rejected",
                "Your meeting request for " + req.getPreferredDate() + " was not approved." +
                (review.getMentorResponseNotes() != null ? " Note: " + review.getMentorResponseNotes() : ""),
                requestId, "MeetingRequest");

        return MeetingRequestResponse.from(req);
    }

    private MeetingRequest getAndValidate(Long requestId, Long mentorUserId) {
        MeetingRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        if (!req.getMentorUserId().equals(mentorUserId))
            throw new com.smms.meeting_service.exception.MeetingException("ACCESS_DENIED",
                    "Request does not belong to you", org.springframework.http.HttpStatus.FORBIDDEN);
        if (req.getStatus() != MeetingRequestStatus.PENDING)
            throw new IllegalStateException("Request is already " + req.getStatus());
        return req;
    }
}
