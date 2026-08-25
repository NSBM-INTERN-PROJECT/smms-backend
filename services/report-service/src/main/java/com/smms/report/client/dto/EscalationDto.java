package com.smms.report.client.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EscalationDto {
    private Long id;
    private Long sessionNoteId;
    private Long mentorUserId;
    private Long studentUserId;
    private String category;        // ACADEMIC / PERSONAL / CAREER / FINANCIAL / ATTENDANCE / HEALTH / OTHER
    private String description;
    private String escalatedToRole; // COORDINATOR / MANAGEMENT / STUDENT_SUPPORT
    private String status;          // OPEN / ACKNOWLEDGED / IN_PROGRESS / RESOLVED / CLOSED
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
