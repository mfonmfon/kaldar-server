package com.kaldar.kaldar.notification.application.service.impl;

import com.kaldar.kaldar.notification.application.dto.response.NotificationResponse;
import com.kaldar.kaldar.notification.application.service.NotificationService;
import com.kaldar.kaldar.notification.domain.model.NotificationEntity;
import com.kaldar.kaldar.notification.domain.repository.NotificationRepository;
import com.kaldar.kaldar.shared.domain.exceptions.NotificationNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;

    public DefaultNotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(Long userId, int page, int limit, String type, Boolean read) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<NotificationEntity> entities;

        if (type != null && read != null) {
            entities = notificationRepository.findByUserIdAndTypeAndIsRead(userId, type, read, pageable);
        } else if (type != null) {
            entities = notificationRepository.findByUserIdAndType(userId, type, pageable);
        } else if (read != null) {
            entities = notificationRepository.findByUserIdAndIsRead(userId, read, pageable);
        } else {
            entities = notificationRepository.findByUserId(userId, pageable);
        }

        return entities.map(this::toResponse);
    }

    @Override
    @Transactional
    public void markRead(Long userId, Long notificationId, boolean isRead) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification not found with id: " + notificationId));
        notification.setRead(isRead);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification not found with id: " + notificationId));
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void bulkMarkRead(Long userId, List<Long> ids) {
        List<NotificationEntity> notifications = notificationRepository.findAllById(ids).stream()
                .filter(n -> n.getUserId().equals(userId))
                .toList();
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void bulkMarkUnread(Long userId, List<Long> ids) {
        List<NotificationEntity> notifications = notificationRepository.findAllById(ids).stream()
                .filter(n -> n.getUserId().equals(userId))
                .toList();
        notifications.forEach(n -> n.setRead(false));
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void bulkDelete(Long userId, List<Long> ids) {
        List<NotificationEntity> notifications = notificationRepository.findAllById(ids).stream()
                .filter(n -> n.getUserId().equals(userId))
                .toList();
        notificationRepository.deleteAll(notifications);
    }

    @Override
    @Transactional
    public void markAllRead(Long userId) {
        List<NotificationEntity> unread = notificationRepository.findByUserIdAndIsRead(userId, false);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    private NotificationResponse toResponse(NotificationEntity entity) {
        return new NotificationResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getType(),
                entity.isRead(),
                entity.getCreatedAt(),
                entity.getMetadata()
        );
    }
}
