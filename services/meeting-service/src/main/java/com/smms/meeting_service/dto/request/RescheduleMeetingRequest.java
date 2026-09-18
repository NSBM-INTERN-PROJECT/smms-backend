package com.smms.meeting_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RescheduleMeetingRequest {
    @NotNull private LocalDate newDate;
    @NotNull private LocalTime newTime;
    private String reason;
}
