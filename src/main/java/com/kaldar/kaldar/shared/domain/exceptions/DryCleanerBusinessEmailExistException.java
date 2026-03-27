package com.kaldar.kaldar.shared.domain.exceptions;

public class DryCleanerBusinessEmailExistException extends RuntimeException {
    public DryCleanerBusinessEmailExistException(String message) {
        super(message);
    }
}
