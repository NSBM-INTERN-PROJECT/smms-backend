package com.smms.report.client.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AllocationDto {
    private Long id;
    private Long mentorUserId;
    private Long studentUserId;
    private String allocationType;  // MANUAL / RANDOM
    private String status;          // ACTIVE / INACTIVE / TRANSFERRED
    private LocalDate allocationDate;
    private LocalDateTime createdAt;
}
