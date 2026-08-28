package com.smms.report.dto.response;

public record AllocationStatisticsResponse(
        long allocatedStudents,
        long unallocatedStudents
) {
}
