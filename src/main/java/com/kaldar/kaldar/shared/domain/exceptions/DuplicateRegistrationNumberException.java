package com.kaldar.kaldar.shared.domain.exceptions;

public class DuplicateRegistrationNumberException extends RuntimeException {
    public DuplicateRegistrationNumberException(String message) {
        super(message);
    }
}
