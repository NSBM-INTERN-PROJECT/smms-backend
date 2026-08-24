package com.smms.meeting_service.dto.response;

import com.smms.meeting_service.domain.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data @Builder
public class MeetingResponse {
    private Long id;
    private Long allocationId;
    private Long mentorUserId;
    private Long studentUserId;
    private String title;
    private String description;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private Integer durationMinutes;
    private String location;
    private MeetingMode mode;
    private String meetingLink;
    private MeetingStatus status;
    private AttendanceStatus attendanceStatus;
    private Boolean reminderSent;
    private Long rescheduledFromId;
    private Integer rescheduleCount;
    private String cancelledReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MeetingResponse from(com.smms.meeting_service.domain.Meeting m) {
        return MeetingResponse.builder()
                .id(m.getId()).allocationId(m.getAllocationId())
                .mentorUserId(m.getMentorUserId()).studentUserId(m.getStudentUserId())
                .title(m.getTitle()).description(m.getDescription())
                .scheduledDate(m.getScheduledDate()).scheduledTime(m.getScheduledTime())
                .durationMinutes(m.getDurationMinutes()).location(m.getLocation())
                .mode(m.getMode()).meetingLink(m.getMeetingLink())
                .status(m.getStatus()).attendanceStatus(m.getAttendanceStatus())
                .reminderSent(m.getReminderSent()).rescheduledFromId(m.getRescheduledFromId())
                .rescheduleCount(m.getRescheduleCount()).cancelledReason(m.getCancelledReason())
                .createdAt(m.getCreatedAt()).updatedAt(m.getUpdatedAt()).build();
    }
}
