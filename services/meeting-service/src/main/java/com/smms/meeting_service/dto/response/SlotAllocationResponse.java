package com.smms.meeting_service.dto.response;

import com.smms.meeting_service.domain.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class SlotAllocationResponse {
    private Long id;
    private Long slotId;
    private Long studentUserId;
    private SlotAllocationStatus status;
    private StudentResponse studentResponse;
    private String rescheduleReason;
    private LocalDateTime respondedAt;
    private LocalDateTime createdAt;

    public static SlotAllocationResponse from(MeetingSlotAllocation a) {
        return SlotAllocationResponse.builder()
                .id(a.getId()).slotId(a.getSlotId()).studentUserId(a.getStudentUserId())
                .status(a.getStatus()).studentResponse(a.getStudentResponse())
                .rescheduleReason(a.getRescheduleReason()).respondedAt(a.getRespondedAt())
                .createdAt(a.getCreatedAt()).build();
    }
}
