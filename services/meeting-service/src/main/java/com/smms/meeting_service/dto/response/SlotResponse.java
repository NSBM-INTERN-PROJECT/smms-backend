package com.smms.meeting_service.dto.response;

import com.smms.meeting_service.domain.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data @Builder
public class SlotResponse {
    private Long id;
    private Long mentorUserId;
    private LocalDate slotDate;
    private LocalTime startTime;
    private Integer durationMinutes;
    private MeetingMode mode;
    private String location;
    private String meetingLink;
    private SlotStatus status;
    private String filterCriteria;
    private LocalDateTime createdAt;
    private Long allocationId;
    private Long assignedStudentUserId;
    private String assignedStudentName;
    private SlotAllocationStatus allocationStatus;

    public LocalTime getEndTime() {
        if (startTime != null && durationMinutes != null) {
            return startTime.plusMinutes(durationMinutes);
        }
        return startTime;
    }

    public static SlotResponse from(MeetingSlot s) {
        return SlotResponse.builder()
                .id(s.getId()).mentorUserId(s.getMentorUserId())
                .slotDate(s.getSlotDate()).startTime(s.getStartTime())
                .durationMinutes(s.getDurationMinutes()).mode(s.getMode())
                .location(s.getLocation()).meetingLink(s.getMeetingLink())
                .status(s.getStatus()).filterCriteria(s.getFilterCriteria())
                .createdAt(s.getCreatedAt()).build();
    }

    public static SlotResponse from(MeetingSlot s, MeetingSlotAllocation a) {
        SlotResponse resp = from(s);
        if (a != null) {
            resp.setAllocationId(a.getId());
            resp.setAssignedStudentUserId(a.getStudentUserId());
            resp.setAllocationStatus(a.getStatus());
        }
        return resp;
    }
}

