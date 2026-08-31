package com.smms.report.client.dto;

import lombok.Data;

/** Lightweight summary returned by user-service internal endpoint. */
@Data
public class MentorSummaryDto {
    private Long userId;
    private String fullName;
    private String email;
    private String department;
    private Integer maxStudents;
    private String specialization;
}
