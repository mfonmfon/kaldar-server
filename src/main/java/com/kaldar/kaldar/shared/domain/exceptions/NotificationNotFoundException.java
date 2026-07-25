package com.kaldar.kaldar.shared.domain.exceptions;

import org.springframework.http.HttpStatus;

public class NotificationNotFoundException extends KaldarBusinessException {
    public NotificationNotFoundException(String message) {
        super("NOTIFICATION_NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
