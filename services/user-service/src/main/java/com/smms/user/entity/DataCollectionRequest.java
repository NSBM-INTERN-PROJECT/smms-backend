package com.smms.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "data_collection_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataCollectionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mentor_user_id", nullable = false)
    private Long mentorUserId;

    @Column(name = "filter_criteria", columnDefinition = "JSON")
    private String filterCriteria;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
