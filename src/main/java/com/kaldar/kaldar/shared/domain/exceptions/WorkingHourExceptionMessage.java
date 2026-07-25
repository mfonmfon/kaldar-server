package com.kaldar.kaldar.shared.domain.exceptions;

import org.springframework.http.HttpStatus;

public class WorkingHourExceptionMessage extends KaldarBusinessException {
    public WorkingHourExceptionMessage(String message) {
        super("INVALID_WORKING_HOURS", message, HttpStatus.BAD_REQUEST);
    }
}
