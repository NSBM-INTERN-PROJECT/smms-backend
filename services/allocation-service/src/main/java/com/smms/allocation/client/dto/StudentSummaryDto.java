package com.smms.allocation.client.dto;

import lombok.Data;

/** Lightweight DTO received from user-service for allocation purposes. */
@Data
public class StudentSummaryDto {
    private Long userId;
    private String fullName;
    private String batch;
    private String department;
}
