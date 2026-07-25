package com.kaldar.kaldar.notification.service;

import com.kaldar.kaldar.notification.application.dto.response.NotificationResponse;
import com.kaldar.kaldar.notification.application.service.impl.DefaultNotificationService;
import com.kaldar.kaldar.notification.domain.model.NotificationEntity;
import com.kaldar.kaldar.notification.domain.repository.NotificationRepository;
import com.kaldar.kaldar.shared.domain.exceptions.NotificationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultNotificationService Unit Tests")
class DefaultNotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;

    private DefaultNotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new DefaultNotificationService(notificationRepository);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private NotificationEntity buildNotification(Long id, Long userId, boolean isRead) {
        NotificationEntity n = new NotificationEntity();
        n.setId(id);
        n.setUserId(userId);
        n.setTitle("Test Title");
        n.setMessage("Test Message");
        n.setType("ORDER_UPDATE");
        n.setRead(isRead);
        n.setCreatedAt(LocalDateTime.now());
        return n;
    }

    // =========================================================================
    // getNotifications
    // =========================================================================

    @Nested
    @DisplayName("getNotifications()")
    class GetNotifications {

        @Test
        @DisplayName("should return paginated notifications with no filters")
        void shouldReturnPaginatedNotificationsWithNoFilter() {
            NotificationEntity entity = buildNotification(1L, 10L, false);
            Page<NotificationEntity> page = new PageImpl<>(List.of(entity));

            when(notificationRepository.findByUserId(eq(10L), any(Pageable.class))).thenReturn(page);

            Page<NotificationResponse> result = notificationService.getNotifications(10L, 0, 10, null, null);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
            assertThat(result.getContent().get(0).isRead()).isFalse();
        }

        @Test
        @DisplayName("should filter by type only when type is given and read is null")
        void shouldFilterByTypeOnly() {
            NotificationEntity entity = buildNotification(2L, 10L, false);
            Page<NotificationEntity> page = new PageImpl<>(List.of(entity));

            when(notificationRepository.findByUserIdAndType(eq(10L), eq("ORDER_UPDATE"), any(Pageable.class)))
                    .thenReturn(page);

            Page<NotificationResponse> result = notificationService.getNotifications(10L, 0, 10, "ORDER_UPDATE", null);

            assertThat(result.getContent()).hasSize(1);
            verify(notificationRepository).findByUserIdAndType(eq(10L), eq("ORDER_UPDATE"), any(Pageable.class));
        }

        @Test
        @DisplayName("should filter by read status only when type is null and read is given")
        void shouldFilterByReadStatusOnly() {
            NotificationEntity entity = buildNotification(3L, 10L, false);
            Page<NotificationEntity> page = new PageImpl<>(List.of(entity));

            when(notificationRepository.findByUserIdAndIsRead(eq(10L), eq(false), any(Pageable.class)))
                    .thenReturn(page);

            Page<NotificationResponse> result = notificationService.getNotifications(10L, 0, 10, null, false);

            assertThat(result.getContent()).hasSize(1);
            verify(notificationRepository).findByUserIdAndIsRead(eq(10L), eq(false), any(Pageable.class));
        }

        @Test
        @DisplayName("should filter by both type and read when both are given")
        void shouldFilterByTypeAndRead() {
            Page<NotificationEntity> page = new PageImpl<>(List.of());

            when(notificationRepository.findByUserIdAndTypeAndIsRead(eq(10L), eq("PROMO"), eq(true), any(Pageable.class)))
                    .thenReturn(page);

            Page<NotificationResponse> result = notificationService.getNotifications(10L, 0, 10, "PROMO", true);

            assertThat(result.getContent()).isEmpty();
            verify(notificationRepository).findByUserIdAndTypeAndIsRead(eq(10L), eq("PROMO"), eq(true), any(Pageable.class));
        }
    }

    // =========================================================================
    // markRead
    // =========================================================================

    @Nested
    @DisplayName("markRead()")
    class MarkRead {

        @Test
        @DisplayName("should set isRead to true on the notification")
        void shouldMarkNotificationAsRead() {
            NotificationEntity entity = buildNotification(1L, 10L, false);
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            notificationService.markRead(10L, 1L, true);

            ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
            verify(notificationRepository).save(captor.capture());
            assertThat(captor.getValue().isRead()).isTrue();
        }

        @Test
        @DisplayName("should set isRead to false on the notification")
        void shouldMarkNotificationAsUnread() {
            NotificationEntity entity = buildNotification(1L, 10L, true);
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            notificationService.markRead(10L, 1L, false);

            ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
            verify(notificationRepository).save(captor.capture());
            assertThat(captor.getValue().isRead()).isFalse();
        }

        @Test
        @DisplayName("should throw NotificationNotFoundException when notification does not exist")
        void shouldThrowWhenNotificationNotFound() {
            when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> notificationService.markRead(10L, 99L, true))
                    .isInstanceOf(NotificationNotFoundException.class);
        }

        @Test
        @DisplayName("should throw NotificationNotFoundException when notification belongs to a different user")
        void shouldThrowWhenNotificationBelongsToDifferentUser() {
            // notification belongs to userId=99, but caller is userId=10
            NotificationEntity entity = buildNotification(1L, 99L, false);
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(entity));

            assertThatThrownBy(() -> notificationService.markRead(10L, 1L, true))
                    .isInstanceOf(NotificationNotFoundException.class);
        }
    }

    // =========================================================================
    // deleteNotification
    // =========================================================================

    @Nested
    @DisplayName("deleteNotification()")
    class DeleteNotification {

        @Test
        @DisplayName("should delete the notification when it belongs to the user")
        void shouldDeleteNotification() {
            NotificationEntity entity = buildNotification(1L, 10L, false);
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(entity));

            notificationService.deleteNotification(10L, 1L);

            verify(notificationRepository).delete(entity);
        }

        @Test
        @DisplayName("should throw NotificationNotFoundException when notification does not exist")
        void shouldThrowWhenNotificationNotFound() {
            when(notificationRepository.findById(55L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> notificationService.deleteNotification(10L, 55L))
                    .isInstanceOf(NotificationNotFoundException.class);
        }

        @Test
        @DisplayName("should throw NotificationNotFoundException when caller does not own the notification")
        void shouldThrowWhenCallerDoesNotOwnNotification() {
            NotificationEntity entity = buildNotification(1L, 99L, false);
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(entity));

            assertThatThrownBy(() -> notificationService.deleteNotification(10L, 1L))
                    .isInstanceOf(NotificationNotFoundException.class);
        }
    }

    // =========================================================================
    // bulkMarkRead / bulkMarkUnread
    // =========================================================================

    @Nested
    @DisplayName("bulkMarkRead()")
    class BulkMarkRead {

        @Test
        @DisplayName("should mark all owned notifications as read")
        void shouldBulkMarkOwnedNotificationsAsRead() {
            NotificationEntity n1 = buildNotification(1L, 10L, false);
            NotificationEntity n2 = buildNotification(2L, 10L, false);
            // n3 belongs to a different user — must be silently excluded
            NotificationEntity n3 = buildNotification(3L, 99L, false);

            when(notificationRepository.findAllById(List.of(1L, 2L, 3L)))
                    .thenReturn(List.of(n1, n2, n3));

            notificationService.bulkMarkRead(10L, List.of(1L, 2L, 3L));

            ArgumentCaptor<List<NotificationEntity>> captor = ArgumentCaptor.forClass(List.class);
            verify(notificationRepository).saveAll(captor.capture());

            List<NotificationEntity> saved = captor.getValue();
            assertThat(saved).hasSize(2);
            assertThat(saved).allSatisfy(n -> assertThat(n.isRead()).isTrue());
        }
    }

    @Nested
    @DisplayName("bulkMarkUnread()")
    class BulkMarkUnread {

        @Test
        @DisplayName("should mark all owned notifications as unread")
        void shouldBulkMarkOwnedNotificationsAsUnread() {
            NotificationEntity n1 = buildNotification(1L, 10L, true);
            NotificationEntity n2 = buildNotification(2L, 10L, true);

            when(notificationRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(n1, n2));

            notificationService.bulkMarkUnread(10L, List.of(1L, 2L));

            ArgumentCaptor<List<NotificationEntity>> captor = ArgumentCaptor.forClass(List.class);
            verify(notificationRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).allSatisfy(n -> assertThat(n.isRead()).isFalse());
        }
    }

    // =========================================================================
    // bulkDelete
    // =========================================================================

    @Nested
    @DisplayName("bulkDelete()")
    class BulkDelete {

        @Test
        @DisplayName("should delete only notifications owned by the caller")
        void shouldDeleteOnlyOwnedNotifications() {
            NotificationEntity n1 = buildNotification(1L, 10L, false);
            NotificationEntity n2 = buildNotification(2L, 99L, false); // different user

            when(notificationRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(n1, n2));

            notificationService.bulkDelete(10L, List.of(1L, 2L));

            ArgumentCaptor<List<NotificationEntity>> captor = ArgumentCaptor.forClass(List.class);
            verify(notificationRepository).deleteAll(captor.capture());
            assertThat(captor.getValue()).hasSize(1);
            assertThat(captor.getValue().get(0).getId()).isEqualTo(1L);
        }
    }

    // =========================================================================
    // markAllRead
    // =========================================================================

    @Nested
    @DisplayName("markAllRead()")
    class MarkAllRead {

        @Test
        @DisplayName("should mark every unread notification for the user as read")
        void shouldMarkAllUnreadAsRead() {
            NotificationEntity n1 = buildNotification(1L, 10L, false);
            NotificationEntity n2 = buildNotification(2L, 10L, false);

            when(notificationRepository.findByUserIdAndIsRead(10L, false)).thenReturn(List.of(n1, n2));

            notificationService.markAllRead(10L);

            ArgumentCaptor<List<NotificationEntity>> captor = ArgumentCaptor.forClass(List.class);
            verify(notificationRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).allSatisfy(n -> assertThat(n.isRead()).isTrue());
        }

        @Test
        @DisplayName("should save nothing when there are no unread notifications")
        void shouldSaveNothingWhenNoUnread() {
            when(notificationRepository.findByUserIdAndIsRead(10L, false)).thenReturn(List.of());

            notificationService.markAllRead(10L);

            verify(notificationRepository).saveAll(List.of());
        }
    }
}
