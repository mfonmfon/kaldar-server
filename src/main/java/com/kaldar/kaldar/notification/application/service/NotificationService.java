package com.kaldar.kaldar.notification.application.service;

import com.kaldar.kaldar.notification.application.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NotificationService {

    Page<NotificationResponse> getNotifications(Long userId, int page, int limit, String type, Boolean read);

    void markRead(Long userId, Long notificationId, boolean isRead);

    void deleteNotification(Long userId, Long notificationId);

    void bulkMarkRead(Long userId, List<Long> ids);

    void bulkMarkUnread(Long userId, List<Long> ids);

    void bulkDelete(Long userId, List<Long> ids);

    void markAllRead(Long userId);
}
