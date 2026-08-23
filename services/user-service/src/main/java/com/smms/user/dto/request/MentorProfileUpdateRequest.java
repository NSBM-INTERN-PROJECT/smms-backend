package com.smms.user.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    private String department;

    private String specialization;

    private String phone;

    @Min(value = 1, message = "Max students must be at least 1")
    @Max(value = 20, message = "Max students cannot exceed 20")
    private Integer maxStudents;

    private Boolean isActive;
}