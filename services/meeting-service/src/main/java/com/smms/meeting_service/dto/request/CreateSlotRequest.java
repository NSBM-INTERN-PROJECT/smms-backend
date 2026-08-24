package com.smms.meeting_service.dto.request;

import com.smms.meeting_service.domain.MeetingMode;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateSlotRequest {
    @NotNull private LocalDate slotDate;
    @NotNull private LocalTime startTime;
    @Min(15) @Max(180) private int durationMinutes = 30;
    @NotNull private MeetingMode mode;
    private String location;
    private String meetingLink;
    /** Optional JSON string of filter criteria (e.g. batch, department). */
    private String filterCriteria;
}
