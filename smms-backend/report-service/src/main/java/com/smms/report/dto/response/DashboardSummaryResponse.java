package com.smms.report.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record DashboardSummaryResponse(
        long totalStudents,
        long totalMentors,
        long allocatedStudents,
        long unallocatedStudents,
        long totalMeetings,
        long completedMeetings,
        long pendingMeetings,
        BigDecimal attendanceRate,
        long atRiskStudents,
        long openEscalations,
        Instant generatedAt
) {
}
