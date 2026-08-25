package com.smms.user.dto.response;

import com.smms.user.domain.RiskStatus;
import com.smms.user.domain.StudentProfile;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
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
    private RiskStatus riskStatus;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StudentProfileResponse from(StudentProfile s) {
        return StudentProfileResponse.builder()
                .id(s.getId()).userId(s.getUserId()).fullName(s.getFullName())
                .studentId(s.getStudentId()).email(s.getEmail()).phone(s.getPhone())
                .degreeProgram(s.getDegreeProgram()).department(s.getDepartment())
                .batch(s.getBatch()).intake(s.getIntake()).academicYear(s.getAcademicYear())
                .riskStatus(s.getRiskStatus()).isActive(s.getIsActive())
                .createdAt(s.getCreatedAt()).updatedAt(s.getUpdatedAt()).build();
    }
}
