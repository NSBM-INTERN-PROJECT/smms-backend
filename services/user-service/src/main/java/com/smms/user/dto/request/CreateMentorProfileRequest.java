package com.smms.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateMentorProfileRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Employee ID is required")
    @Size(max = 20)
    private String employeeId;

    @NotBlank(message = "Department is required")
    @Size(max = 100)
    private String department;

    @Size(max = 150)
    private String specialization;

    @Size(max = 20)
    private String phone;

    @Min(1) @Max(20)
    private Integer maxStudents = 5;
}
