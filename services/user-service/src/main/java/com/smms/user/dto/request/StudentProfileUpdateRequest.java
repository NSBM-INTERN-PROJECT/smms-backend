package com.smms.user.dto.request;

import com.smms.user.entity.StudentProfile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String phone;
    private String degreeProgram;
    private String department;
    private String batch;
    private String intake;
    private Integer academicYear;
    private StudentProfile.RiskStatus riskStatus;
    private Boolean isActive;
}