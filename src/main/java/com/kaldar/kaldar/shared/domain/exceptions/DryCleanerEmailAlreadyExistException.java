package com.kaldar.kaldar.shared.domain.exceptions;

public class DryCleanerEmailAlreadyExistException extends RuntimeException {
    public DryCleanerEmailAlreadyExistException(String message) {
        super(message);
    }
}
