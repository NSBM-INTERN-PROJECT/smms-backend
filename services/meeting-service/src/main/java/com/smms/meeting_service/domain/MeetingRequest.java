package com.smms.meeting_service.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "meeting_requests")
@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class MeetingRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_user_id", nullable = false) private Long studentUserId;
    @Column(name = "mentor_user_id",  nullable = false) private Long mentorUserId;
    @Column(name = "preferred_date",  nullable = false) private LocalDate preferredDate;
    @Column(name = "preferred_time",  nullable = false) private LocalTime preferredTime;
    @Column(columnDefinition = "TEXT") private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15) @Builder.Default private MeetingRequestStatus status = MeetingRequestStatus.PENDING;

    @Column(name = "mentor_response_notes", length = 500) private String mentorResponseNotes;
    @Column(name = "responded_at") private LocalDateTime respondedAt;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
