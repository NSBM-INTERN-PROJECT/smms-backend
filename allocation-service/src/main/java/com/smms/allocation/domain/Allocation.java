package com.smms.allocation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "allocations")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Allocation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mentor_user_id", nullable = false)
    private Long mentorUserId;

    @Column(name = "student_user_id", nullable = false)
    private Long studentUserId;

    @Column(name = "coordinator_user_id", nullable = false)
    private Long coordinatorUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "allocation_type", nullable = false, length = 10)
    @Builder.Default
    private AllocationType allocationType = AllocationType.MANUAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private AllocationStatus status = AllocationStatus.ACTIVE;

    @Column(name = "allocated_date", nullable = false)
    private LocalDate allocatedDate;

    @Column(name = "deactivated_date")
    private LocalDate deactivatedDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
