package com.kaldar.kaldar.notification.api;

import com.kaldar.kaldar.notification.application.dto.request.BulkNotificationRequest;
import com.kaldar.kaldar.notification.application.dto.request.ToggleReadRequest;
import com.kaldar.kaldar.notification.application.dto.response.NotificationResponse;
import com.kaldar.kaldar.notification.application.service.NotificationService;
import com.kaldar.kaldar.shared.api.response.ApiResponse;
import com.kaldar.kaldar.shared.infrastructure.utility.CurrentUserResolver;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserResolver currentUserResolver;

    public NotificationController(NotificationService notificationService,
                                   CurrentUserResolver currentUserResolver) {
        this.notificationService = notificationService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean read) {

        Long userId = currentUserResolver.getCurrentUserId();
        Page<NotificationResponse> result = notificationService.getNotifications(userId, page, limit, type, read);
        ApiResponse<Page<NotificationResponse>> response = ApiResponse.<Page<NotificationResponse>>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Notifications retrieved")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(
            @PathVariable Long id,
            @RequestBody ToggleReadRequest request) {

        Long userId = currentUserResolver.getCurrentUserId();
        notificationService.markRead(userId, id, request.isRead());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(NOTIFICATION_READ_UPDATED.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
        Long userId = currentUserResolver.getCurrentUserId();
        notificationService.deleteNotification(userId, id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(NOTIFICATION_DELETED.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bulk/read")
    public ResponseEntity<ApiResponse<Void>> bulkMarkRead(@RequestBody BulkNotificationRequest request) {
        Long userId = currentUserResolver.getCurrentUserId();
        notificationService.bulkMarkRead(userId, request.getIds());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(NOTIFICATIONS_BULK_UPDATED.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bulk/unread")
    public ResponseEntity<ApiResponse<Void>> bulkMarkUnread(@RequestBody BulkNotificationRequest request) {
        Long userId = currentUserResolver.getCurrentUserId();
        notificationService.bulkMarkUnread(userId, request.getIds());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(NOTIFICATIONS_BULK_UPDATED.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bulk/delete")
    public ResponseEntity<ApiResponse<Void>> bulkDelete(@RequestBody BulkNotificationRequest request) {
        Long userId = currentUserResolver.getCurrentUserId();
        notificationService.bulkDelete(userId, request.getIds());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(NOTIFICATION_DELETED.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead() {
        Long userId = currentUserResolver.getCurrentUserId();
        notificationService.markAllRead(userId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(NOTIFICATIONS_ALL_READ.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }
}
