package com.kaldar.kaldar.exceptions;

public class ExpiredOtpException extends RuntimeException {
    public ExpiredOtpException(String message) {
        super(message);
    }
}
