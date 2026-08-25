package com.smms.session.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SessionRequest {
    
    @NotNull(message = "Meeting ID is required")
    private Long meetingId;

    private String notes;

    @NotBlank(message = "Attendance status is required")
    private String attendanceStatus;
}