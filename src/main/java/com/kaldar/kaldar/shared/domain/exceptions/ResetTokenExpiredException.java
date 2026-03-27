package com.kaldar.kaldar.shared.domain.exceptions;

public class ResetTokenExpiredException extends RuntimeException {
    public ResetTokenExpiredException(String message) {
        super(message);
    }
}

