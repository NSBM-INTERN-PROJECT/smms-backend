package com.smms.meeting.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Builder
public class MeetingResponse {
    private Long id;
    private Long allocationId;
    private String title;
    private String description;
    private LocalDate meetingDate;
    private LocalTime meetingTime;
    private String locationOrLink;
    private String status;
    private LocalDateTime createdAt;
}