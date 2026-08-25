package com.smms.report.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Student-specific dashboard: their own progress and meeting stats.
 */
@Data @Builder
public class StudentDashboard {
    private Long studentUserId;
    private Long mentorUserId;
    private String latestProgressStatus;  // ON_TRACK / NEEDS_ATTENTION / AT_RISK / CRITICAL
    private long totalMeetings;
    private long completedMeetings;
    private long upcomingMeetings;
    private long attendancePresent;
    private long attendanceAbsent;
    private long openEscalations;
    private long totalSessionNotes;
}
