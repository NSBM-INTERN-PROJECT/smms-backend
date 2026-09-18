package com.smms.session.dto.response;

import com.smms.session.domain.ProgressStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lightweight summary for a student's latest progress status.
 * Used in the mentor dashboard and report-service.
 */
@Data @Builder
public class StudentProgressSummary {
    private Long studentUserId;
    private Long mentorUserId;
    private ProgressStatus latestProgressStatus;
    private LocalDate latestFollowUpDate;
    private Long openEscalations;
    private LocalDateTime lastNoteAt;
}
