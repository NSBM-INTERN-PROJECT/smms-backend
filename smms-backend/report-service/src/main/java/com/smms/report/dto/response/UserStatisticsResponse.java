package com.smms.report.dto.response;

public record UserStatisticsResponse(
        long totalStudents,
        long totalMentors
) {
}
