package com.smms.meeting_service.dto.request;

import com.smms.meeting_service.domain.MeetingMode;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReviewMeetingRequest {
    @Size(max = 500) private String mentorResponseNotes;
    @Size(max = 500) private String reviewNotes;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private MeetingMode mode;
    private String location;
    private String meetingLink;

    public String getEffectiveNotes() {
        return reviewNotes != null ? reviewNotes : mentorResponseNotes;
    }
}
