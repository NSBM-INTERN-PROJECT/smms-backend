package com.smms.session.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SessionResponse {
    private Long id;
    private Long meetingId;
    private String notes;
    private String attendanceStatus;
    private LocalDateTime createdAt;
}