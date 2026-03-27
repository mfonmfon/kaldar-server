package com.kaldar.kaldar.shared.domain.exceptions;

public class OTPNotFoundException extends RuntimeException {
    public OTPNotFoundException(String message) {
        super(message);
    }
}
