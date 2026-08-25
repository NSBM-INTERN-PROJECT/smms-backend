package com.smms.meeting_service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewMeetingRequest {
    @Size(max = 500) private String mentorResponseNotes;
}
