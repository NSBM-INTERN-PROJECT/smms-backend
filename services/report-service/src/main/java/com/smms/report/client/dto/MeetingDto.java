package com.smms.report.client.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class MeetingDto {
    private Long id;
    private Long allocationId;
    private Long mentorUserId;
    private Long studentUserId;
    private String title;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private Integer durationMinutes;
    private String mode;              // PHYSICAL / ONLINE / HYBRID
    private String status;            // SCHEDULED / COMPLETED / CANCELLED / RESCHEDULED
    private String attendanceStatus;  // PENDING / PRESENT / ABSENT / LATE / EXCUSED
    private LocalDateTime createdAt;
}
