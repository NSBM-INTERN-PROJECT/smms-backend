package com.smms.report.dto.response;

public record SessionStatisticsResponse(
        long atRiskStudents,
        long openEscalations
) {
}
