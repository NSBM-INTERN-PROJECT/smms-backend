package com.smms.meeting_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MeetingRequestDto {
    @NotNull private Long mentorUserId;
    @NotNull private LocalDate preferredDate;
    @NotNull private LocalTime preferredTime;
    @Size(max = 1000) private String reason;
}
