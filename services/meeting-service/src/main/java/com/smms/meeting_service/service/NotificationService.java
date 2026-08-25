package com.smms.meeting_service.service;

import com.smms.meeting_service.domain.Notification;
import com.smms.meeting_service.domain.NotificationType;
import com.smms.meeting_service.dto.response.NotificationResponse;
import com.smms.meeting_service.dto.response.PagedResponse;
import com.smms.meeting_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepo;

    @Transactional
    public void push(Long recipientUserId, NotificationType type,
                     String title, String message, Long referenceId, String referenceType) {
        Notification notif = Notification.builder()
                .recipientUserId(recipientUserId).type(type)
                .title(title).message(message)
                .referenceId(referenceId).referenceType(referenceType)
                .isRead(false).emailSent(false).build();
        notificationRepo.save(notif);
        log.debug("Notification [{}] pushed to userId={}", type, recipientUserId);
    }

    @Transactional(readOnly = true)
    public PagedResponse<NotificationResponse> getMyNotifications(Long userId, int page, int size) {
        return PagedResponse.from(
                notificationRepo.findByRecipientUserIdOrderByCreatedAtDesc(
                        userId, PageRequest.of(page, size, Sort.by("createdAt").descending())),
                NotificationResponse::from);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepo.countByRecipientUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepo.markAllReadForUser(userId);
    }
}
