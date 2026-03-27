package com.kaldar.kaldar.shared.domain.exceptions;

public class ExpiredOtpException extends RuntimeException {
    public ExpiredOtpException(String message) {
        super(message);
    }
}
