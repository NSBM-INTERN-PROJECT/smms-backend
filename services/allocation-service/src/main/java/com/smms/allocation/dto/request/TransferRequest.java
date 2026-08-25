package com.smms.allocation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferRequest {

    @NotNull(message = "newMentorUserId is required")
    private Long newMentorUserId;

    private String notes;
}
