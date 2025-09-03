package com.kaldar.kaldar.exceptions;

public class DryCleanerEmailAlreadyExistException extends RuntimeException {
    public DryCleanerEmailAlreadyExistException(String message) {
        super(message);
    }
}
