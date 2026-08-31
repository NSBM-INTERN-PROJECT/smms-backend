package com.smms.session.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "session_notes")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SessionNote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meeting_id", nullable = false, unique = true)
    private Long meetingId;

    @Column(name = "mentor_user_id", nullable = false)
    private Long mentorUserId;

    @Column(name = "student_user_id", nullable = false)
    private Long studentUserId;

    @Column(name = "discussion_notes", nullable = false, columnDefinition = "TEXT")
    private String discussionNotes;

    @Column(name = "action_items", columnDefinition = "TEXT")
    private String actionItems;

    @Enumerated(EnumType.STRING)
    @Column(name = "progress_status", nullable = false, length = 20)
    @Builder.Default
    private ProgressStatus progressStatus = ProgressStatus.ON_TRACK;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "is_private", nullable = false)
    @Builder.Default
    private Boolean isPrivate = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
