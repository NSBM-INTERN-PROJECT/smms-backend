package com.smms.allocation.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AllocationResponse {
    private Long id;
    private String studentId;
    private String mentorId;
    private String academicYear;
    private String status;
    private LocalDateTime createdAt;
}