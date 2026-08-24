package com.smms.session.dto.response;

import com.smms.session.domain.ProgressStatus;
import com.smms.session.domain.SessionNote;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class SessionNoteResponse {
    private Long id;
    private Long meetingId;
    private Long mentorUserId;
    private Long studentUserId;
    private String discussionNotes;
    private String actionItems;
    private ProgressStatus progressStatus;
    private LocalDate followUpDate;
    private Boolean isPrivate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SessionNoteResponse from(SessionNote s) {
        return SessionNoteResponse.builder()
                .id(s.getId()).meetingId(s.getMeetingId())
                .mentorUserId(s.getMentorUserId()).studentUserId(s.getStudentUserId())
                .discussionNotes(s.getDiscussionNotes()).actionItems(s.getActionItems())
                .progressStatus(s.getProgressStatus()).followUpDate(s.getFollowUpDate())
                .isPrivate(s.getIsPrivate())
                .createdAt(s.getCreatedAt()).updatedAt(s.getUpdatedAt()).build();
    }

    /** Returns a redacted view (strips discussion_notes and action_items) for non-private access. */
    public static SessionNoteResponse fromRedacted(SessionNote s) {
        return SessionNoteResponse.builder()
                .id(s.getId()).meetingId(s.getMeetingId())
                .mentorUserId(s.getMentorUserId()).studentUserId(s.getStudentUserId())
                .discussionNotes("[Private]").actionItems(null)
                .progressStatus(s.getProgressStatus()).followUpDate(s.getFollowUpDate())
                .isPrivate(true)
                .createdAt(s.getCreatedAt()).updatedAt(s.getUpdatedAt()).build();
    }
}
