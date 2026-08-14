package com.smms.allocation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AllocationRequest {
    
    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotBlank(message = "Mentor ID is required")
    private String mentorId;

    @NotBlank(message = "Academic Year is required")
    private String academicYear;
}