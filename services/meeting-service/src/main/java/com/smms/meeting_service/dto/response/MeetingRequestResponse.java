package com.smms.meeting_service.dto.response;

import com.smms.meeting_service.domain.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data @Builder
public class MeetingRequestResponse {
    private Long id;
    private Long studentUserId;
    private Long mentorUserId;
    private LocalDate preferredDate;
    private LocalTime preferredTime;
    private String reason;
    private MeetingRequestStatus status;
    private String mentorResponseNotes;
    private LocalDateTime respondedAt;
    private LocalDateTime createdAt;

    public static MeetingRequestResponse from(MeetingRequest r) {
        return MeetingRequestResponse.builder()
                .id(r.getId()).studentUserId(r.getStudentUserId()).mentorUserId(r.getMentorUserId())
                .preferredDate(r.getPreferredDate()).preferredTime(r.getPreferredTime())
                .reason(r.getReason()).status(r.getStatus())
                .mentorResponseNotes(r.getMentorResponseNotes())
                .respondedAt(r.getRespondedAt()).createdAt(r.getCreatedAt()).build();
    }
}
