package com.kaldar.kaldar.exceptions;

public class OTPNotFoundException extends RuntimeException {
    public OTPNotFoundException(String message) {
        super(message);
    }
}
