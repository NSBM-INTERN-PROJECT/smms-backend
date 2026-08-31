package com.smms.session.dto.response;

import com.smms.session.domain.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class EscalationResponse {
    private Long id;
    private Long sessionNoteId;
    private Long mentorUserId;
    private Long studentUserId;
    private EscalationCategory category;
    private String description;
    private EscalationRole escalatedToRole;
    private Long escalatedToUserId;
    private EscalationStatus status;
    private String resolutionNotes;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EscalationResponse from(Escalation e) {
        return EscalationResponse.builder()
                .id(e.getId()).sessionNoteId(e.getSessionNoteId())
                .mentorUserId(e.getMentorUserId()).studentUserId(e.getStudentUserId())
                .category(e.getCategory()).description(e.getDescription())
                .escalatedToRole(e.getEscalatedToRole()).escalatedToUserId(e.getEscalatedToUserId())
                .status(e.getStatus()).resolutionNotes(e.getResolutionNotes())
                .resolvedAt(e.getResolvedAt())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }
}
