package com.smms.user.dto.response;

import com.smms.user.entity.StudentProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponse {

    private Long id;
    private Long userId;
    private String fullName;
    private String studentId;
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