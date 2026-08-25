package com.smms.meeting_service.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "meeting_slots")
@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class MeetingSlot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mentor_user_id", nullable = false) private Long mentorUserId;
    @Column(name = "slot_date", nullable = false) private LocalDate slotDate;
    @Column(name = "start_time", nullable = false) private LocalTime startTime;
    @Column(name = "duration_minutes", nullable = false) @Builder.Default private Integer durationMinutes = 30;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10) @Builder.Default private MeetingMode mode = MeetingMode.PHYSICAL;

    @Column(length = 200) private String location;
    @Column(name = "meeting_link", length = 500) private String meetingLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15) @Builder.Default private SlotStatus status = SlotStatus.OPEN;

    @Column(name = "filter_criteria", columnDefinition = "JSON") private String filterCriteria;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
