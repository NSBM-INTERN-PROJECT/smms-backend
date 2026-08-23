package com.smms.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "data_collection_recipients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataCollectionRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false)
    private Long requestId;

    @Column(name = "student_user_id", nullable = false)
    private Long studentUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipientStatus status;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private Instant sentAt;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @PrePersist
    protected void onCreate() {
        sentAt = Instant.now();
        if (status == null) {
            status = RecipientStatus.PENDING;
        }
    }

    public enum RecipientStatus {
        PENDING, SUBMITTED
    }
}