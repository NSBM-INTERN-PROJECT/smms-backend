package com.smms.meeting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MeetingRequest {
    
    @NotNull(message = "Allocation ID is required")
    private Long allocationId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Meeting date is required")
    private LocalDate meetingDate;

    @NotNull(message = "Meeting time is required")
    private LocalTime meetingTime;

    private String locationOrLink;
}