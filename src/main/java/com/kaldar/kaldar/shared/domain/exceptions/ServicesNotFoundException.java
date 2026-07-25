package com.kaldar.kaldar.shared.domain.exceptions;

import org.springframework.http.HttpStatus;

public class ServicesNotFoundException extends KaldarBusinessException {
    public ServicesNotFoundException(String message) {
        super("SERVICES_NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
