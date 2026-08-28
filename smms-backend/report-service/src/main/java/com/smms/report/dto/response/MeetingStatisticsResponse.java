package com.smms.report.dto.response;

import java.math.BigDecimal;

public record MeetingStatisticsResponse(
        long totalMeetings,
        long completedMeetings,
        long pendingMeetings,
        BigDecimal attendanceRate
) {
}
