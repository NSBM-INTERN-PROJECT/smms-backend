package com.smms.meeting_service.dto.response;

import com.smms.meeting_service.domain.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class NotificationResponse {
    private Long id;
    private Long recipientUserId;
    private NotificationType type;
    private String title;
    private String message;
    private Long referenceId;
    private String referenceType;
    private Boolean isRead;
    private Boolean emailSent;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId()).recipientUserId(n.getRecipientUserId())
                .type(n.getType()).title(n.getTitle()).message(n.getMessage())
                .referenceId(n.getReferenceId()).referenceType(n.getReferenceType())
                .isRead(n.getIsRead()).emailSent(n.getEmailSent())
                .createdAt(n.getCreatedAt()).build();
    }
}
