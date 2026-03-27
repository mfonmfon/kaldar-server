package com.kaldar.kaldar.shared.domain.exceptions;

public class InvalidOtpException extends RuntimeException    {
    public InvalidOtpException(String message) {
        super(message);
    }
}
