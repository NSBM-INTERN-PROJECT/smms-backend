package com.smms.user.dto.response;

import com.smms.user.domain.MentorProfile;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class MentorProfileResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String employeeId;
    private String department;
    private String specialization;
    private String phone;
    private Integer maxStudents;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MentorProfileResponse from(MentorProfile m) {
        return MentorProfileResponse.builder()
                .id(m.getId()).userId(m.getUserId()).fullName(m.getFullName())
                .employeeId(m.getEmployeeId()).department(m.getDepartment())
                .specialization(m.getSpecialization()).phone(m.getPhone())
                .maxStudents(m.getMaxStudents()).isActive(m.getIsActive())
                .createdAt(m.getCreatedAt()).updatedAt(m.getUpdatedAt()).build();
    }
}
