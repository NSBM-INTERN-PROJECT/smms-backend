package com.smms.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorCapacityDto {
    private Long userId;
    private String fullName;
    private String email;
    private String department;
    private String specialization;
    private Integer maxStudents;
}
