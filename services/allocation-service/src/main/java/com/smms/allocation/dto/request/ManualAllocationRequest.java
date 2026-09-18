package com.smms.allocation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ManualAllocationRequest {

    @NotNull(message = "mentorUserId is required")
    private Long mentorUserId;

    @NotNull(message = "studentUserId is required")
    private Long studentUserId;

    private String notes;
}
