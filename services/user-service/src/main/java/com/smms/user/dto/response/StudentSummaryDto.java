package com.smms.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSummaryDto {
    private Long userId;
    private String fullName;
    private String email;
    private String studentId;
    private String batch;
    private String department;
    private String profileStatus;
}
