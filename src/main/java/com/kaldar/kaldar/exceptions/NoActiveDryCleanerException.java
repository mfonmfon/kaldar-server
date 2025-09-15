package com.kaldar.kaldar.exceptions;

import org.springframework.http.HttpStatus;

public class NoActiveDryCleanerException extends KaldarBusinessException {
    public NoActiveDryCleanerException(Long dryCleanerId) {
        super("DRY_CLEANER_NOT_ACTIVE", "No active drycleaner" + dryCleanerId, HttpStatus.BAD_REQUEST);
    }
}
