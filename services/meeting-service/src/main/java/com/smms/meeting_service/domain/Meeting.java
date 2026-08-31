package com.smms.meeting_service.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "meetings")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Meeting {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "allocation_id", nullable = false) private Long allocationId;
    @Column(name = "mentor_user_id", nullable = false) private Long mentorUserId;
    @Column(name = "student_user_id", nullable = false) private Long studentUserId;

    @Column(nullable = false, length = 200) private String title;
    @Column(columnDefinition = "TEXT") private String description;

    @Column(name = "scheduled_date", nullable = false) private LocalDate scheduledDate;
    @Column(name = "scheduled_time", nullable = false) private LocalTime scheduledTime;

    @Column(name = "duration_minutes", nullable = false) @Builder.Default private Integer durationMinutes = 30;
    @Column(length = 200) private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10) @Builder.Default private MeetingMode mode = MeetingMode.PHYSICAL;

    @Column(name = "meeting_link", length = 500) private String meetingLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15) @Builder.Default private MeetingStatus status = MeetingStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", nullable = false, length = 10) @Builder.Default private AttendanceStatus attendanceStatus = AttendanceStatus.PENDING;

    @Column(name = "reminder_sent", nullable = false) @Builder.Default private Boolean reminderSent = false;
    @Column(name = "rescheduled_from_id") private Long rescheduledFromId;
    @Column(name = "reschedule_count", nullable = false) @Builder.Default private Integer rescheduleCount = 0;
    @Column(name = "cancelled_reason", columnDefinition = "TEXT") private String cancelledReason;

    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate  protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
