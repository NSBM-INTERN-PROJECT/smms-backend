package com.smms.report.client.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Mirrors session-service's StudentProgressSummary response. */
@Data
public class StudentProgressSummaryDto {
    private Long studentUserId;
    private Long mentorUserId;
    private String latestProgressStatus;
    private LocalDate latestFollowUpDate;
    private Long openEscalations;
    private LocalDateTime lastNoteAt;
}
