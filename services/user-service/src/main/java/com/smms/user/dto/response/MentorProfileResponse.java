package com.smms.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorProfileResponse {

    private Long id;
    private Long userId;
    private String fullName;
    private String employeeId;
    private String department;
    private String specialization;
    private String phone;
    private Integer maxStudents;
    private Integer currentStudentCount;
    private Boolean isActive;
}