package com.smms.meeting_service.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_slot_allocations")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MeetingSlotAllocation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_id", nullable = false) private Long slotId;
    @Column(name = "student_user_id", nullable = false) private Long studentUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25) @Builder.Default private SlotAllocationStatus status = SlotAllocationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "student_response", length = 25) private StudentResponse studentResponse;

    @Column(name = "reschedule_reason", columnDefinition = "TEXT") private String rescheduleReason;
    @Column(name = "responded_at") private LocalDateTime respondedAt;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
