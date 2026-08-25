package com.smms.report.client.dto;

import lombok.Data;

/** Lightweight summary returned by user-service internal endpoint. */
@Data
public class StudentSummaryDto {
    private Long userId;
    private String fullName;
    private String email;
    private String batch;
    private String department;
    private String profileStatus; // PENDING_REVIEW / APPROVED / REJECTED
}
