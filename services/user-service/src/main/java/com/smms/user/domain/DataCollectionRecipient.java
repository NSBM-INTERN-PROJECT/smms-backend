package com.smms.user.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_collection_recipients")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DataCollectionRecipient {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false)
    private Long requestId;

    @Column(name = "student_user_id", nullable = false)
    private Long studentUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private RecipientStatus status = RecipientStatus.PENDING;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
}
