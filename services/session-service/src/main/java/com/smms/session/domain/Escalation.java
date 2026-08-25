package com.smms.session.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "escalations")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Escalation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_note_id", nullable = false)
    private Long sessionNoteId;

    @Column(name = "mentor_user_id", nullable = false)
    private Long mentorUserId;

    @Column(name = "student_user_id", nullable = false)
    private Long studentUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EscalationCategory category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "escalated_to_role", nullable = false, length = 20)
    private EscalationRole escalatedToRole;

    @Column(name = "escalated_to_user_id")
    private Long escalatedToUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private EscalationStatus status = EscalationStatus.OPEN;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
