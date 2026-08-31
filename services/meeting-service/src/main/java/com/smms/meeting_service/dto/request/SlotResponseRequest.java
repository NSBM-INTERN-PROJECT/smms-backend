package com.smms.meeting_service.dto.request;

import com.smms.meeting_service.domain.StudentResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SlotResponseRequest {
    @NotNull private StudentResponse response;
    private String rescheduleReason;
}
