package com.smms.report.client.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SessionNoteDto {
    private Long id;
    private Long meetingId;
    private Long mentorUserId;
    private Long studentUserId;
    private String progressStatus;  // ON_TRACK / NEEDS_ATTENTION / AT_RISK / CRITICAL
    private LocalDate followUpDate;
    private Boolean isPrivate;
    private LocalDateTime createdAt;
}
