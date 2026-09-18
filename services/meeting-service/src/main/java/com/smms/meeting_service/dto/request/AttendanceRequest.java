package com.smms.meeting_service.dto.request;

import com.smms.meeting_service.domain.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttendanceRequest {
    @NotNull private AttendanceStatus attendanceStatus;
}
