package com.smms.meeting_service.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_user_id", nullable = false) private Long recipientUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30) private NotificationType type;

    @Column(nullable = false, length = 200) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String message;
    @Column(name = "reference_id") private Long referenceId;
    @Column(name = "reference_type", length = 50) private String referenceType;
    @Column(name = "is_read", nullable = false) @Builder.Default private Boolean isRead = false;
    @Column(name = "email_sent", nullable = false) @Builder.Default private Boolean emailSent = false;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
