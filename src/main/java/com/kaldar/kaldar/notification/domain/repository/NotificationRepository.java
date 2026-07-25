package com.kaldar.kaldar.notification.domain.repository;

import com.kaldar.kaldar.notification.domain.model.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByUserId(Long userId, Pageable pageable);

    Page<NotificationEntity> findByUserIdAndType(Long userId, String type, Pageable pageable);

    Page<NotificationEntity> findByUserIdAndIsRead(Long userId, boolean isRead, Pageable pageable);

    Page<NotificationEntity> findByUserIdAndTypeAndIsRead(Long userId, String type, boolean isRead, Pageable pageable);

    List<NotificationEntity> findByUserIdAndIsRead(Long userId, boolean isRead);

    void deleteByIdAndUserId(Long id, Long userId);
}
