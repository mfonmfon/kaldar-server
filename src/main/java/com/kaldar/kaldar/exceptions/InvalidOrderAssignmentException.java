package com.kaldar.kaldar.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidOrderAssignmentException extends KaldarBusinessException {
    public InvalidOrderAssignmentException(String message) {
        super("INVALID_ORDER_ASSIGNMENT", message, HttpStatus.BAD_REQUEST);
    }
}
