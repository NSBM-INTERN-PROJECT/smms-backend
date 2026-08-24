package com.smms.allocation.client.dto;

import lombok.Data;

/** Lightweight DTO received from user-service — mentor with their max student capacity. */
@Data
public class MentorCapacityDto {
    private Long userId;
    private String fullName;
    private String department;
    private Integer maxStudents;
}
