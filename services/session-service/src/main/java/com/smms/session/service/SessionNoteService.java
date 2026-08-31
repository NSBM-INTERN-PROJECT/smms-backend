package com.smms.session.service;

import com.smms.session.domain.EscalationStatus;
import com.smms.session.domain.ProgressStatus;
import com.smms.session.domain.SessionNote;
import com.smms.session.dto.request.CreateSessionNoteRequest;
import com.smms.session.dto.request.UpdateSessionNoteRequest;
import com.smms.session.dto.response.PagedResponse;
import com.smms.session.dto.response.SessionNoteResponse;
import com.smms.session.dto.response.StudentProgressSummary;
import com.smms.session.exception.AccessDeniedException;
import com.smms.session.exception.DuplicateSessionNoteException;
import com.smms.session.exception.SessionNoteNotFoundException;
import com.smms.session.repository.EscalationRepository;
import com.smms.session.repository.SessionNoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class SessionNoteService {

    private final SessionNoteRepository noteRepo;
    private final EscalationRepository escalationRepo;

    /**
     * Mentor creates a session note after a meeting.
     * Only one note is allowed per meeting (enforced by UNIQUE on meeting_id).
     */
    @Transactional
    public SessionNoteResponse create(Long mentorUserId, CreateSessionNoteRequest req) {
        if (noteRepo.existsByMeetingId(req.getMeetingId()))
            throw new DuplicateSessionNoteException(req.getMeetingId());

        SessionNote note = SessionNote.builder()
                .meetingId(req.getMeetingId())
                .mentorUserId(mentorUserId)
                .studentUserId(req.getStudentUserId())
                .discussionNotes(req.getDiscussionNotes())
                .actionItems(req.getActionItems())
                .progressStatus(req.getProgressStatus() != null ? req.getProgressStatus() : ProgressStatus.ON_TRACK)
                .followUpDate(req.getFollowUpDate())
                .isPrivate(req.isPrivate())
                .build();

        return SessionNoteResponse.from(noteRepo.save(note));
    }

    /** Get note by meeting ID. Private notes redacted for student viewers. */
    @Transactional(readOnly = true)
    public SessionNoteResponse getByMeetingId(Long meetingId, Long viewerUserId, String viewerRole) {
        SessionNote note = noteRepo.findByMeetingId(meetingId)
                .orElseThrow(() -> new SessionNoteNotFoundException(meetingId));
        return resolveVisibility(note, viewerUserId, viewerRole);
    }

    /** Paginated history of all session notes for a student. */
    @Transactional(readOnly = true)
    public PagedResponse<SessionNoteResponse> getStudentHistory(Long studentUserId, Long viewerUserId,
                                                                  String viewerRole, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        boolean isMentor = "MENTOR".equalsIgnoreCase(viewerRole) || "ADMIN".equalsIgnoreCase(viewerRole)
                || "COORDINATOR".equalsIgnoreCase(viewerRole);

        var result = isMentor
                ? noteRepo.findByStudentUserIdOrderByCreatedAtDesc(studentUserId, pageable)
                : noteRepo.findByStudentUserIdAndIsPrivateFalseOrderByCreatedAtDesc(studentUserId, pageable);

        return PagedResponse.from(result, n -> isMentor
                ? SessionNoteResponse.from(n)
                : (n.getIsPrivate() ? SessionNoteResponse.fromRedacted(n) : SessionNoteResponse.from(n)));
    }

    /** Mentor updates their own session note (patch semantics). */
    @Transactional
    public SessionNoteResponse update(Long noteId, Long mentorUserId, UpdateSessionNoteRequest req) {
        SessionNote note = noteRepo.findById(noteId)
                .orElseThrow(() -> new SessionNoteNotFoundException(noteId));
        if (!note.getMentorUserId().equals(mentorUserId))
            throw new AccessDeniedException();

        if (req.getDiscussionNotes() != null) note.setDiscussionNotes(req.getDiscussionNotes());
        if (req.getActionItems() != null)     note.setActionItems(req.getActionItems());
        if (req.getProgressStatus() != null)  note.setProgressStatus(req.getProgressStatus());
        if (req.getFollowUpDate() != null)    note.setFollowUpDate(req.getFollowUpDate());
        if (req.getIsPrivate() != null)       note.setIsPrivate(req.getIsPrivate());

        return SessionNoteResponse.from(noteRepo.save(note));
    }

    /**
     * Builds a StudentProgressSummary for each student the mentor has notes for.
     * Uses the latest note per student + open escalation count.
     */
    @Transactional(readOnly = true)
    public List<StudentProgressSummary> getMentorProgressSummary(Long mentorUserId) {
        return noteRepo.findLatestNotePerStudentForMentor(mentorUserId).stream()
                .map(note -> StudentProgressSummary.builder()
                        .studentUserId(note.getStudentUserId())
                        .mentorUserId(note.getMentorUserId())
                        .latestProgressStatus(note.getProgressStatus())
                        .latestFollowUpDate(note.getFollowUpDate())
                        .openEscalations(escalationRepo.countByStudentUserIdAndStatus(
                                note.getStudentUserId(), EscalationStatus.OPEN))
                        .lastNoteAt(note.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private SessionNoteResponse resolveVisibility(SessionNote note, Long viewerUserId, String viewerRole) {
        boolean isMentorOrAdmin = "MENTOR".equalsIgnoreCase(viewerRole)
                || "ADMIN".equalsIgnoreCase(viewerRole)
                || "COORDINATOR".equalsIgnoreCase(viewerRole)
                || note.getMentorUserId().equals(viewerUserId);
        if (note.getIsPrivate() && !isMentorOrAdmin)
            return SessionNoteResponse.fromRedacted(note);
        return SessionNoteResponse.from(note);
    }
}
