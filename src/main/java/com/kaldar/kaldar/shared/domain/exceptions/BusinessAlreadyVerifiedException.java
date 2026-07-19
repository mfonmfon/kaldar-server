package com.kaldar.kaldar.shared.domain.exceptions;

public class BusinessAlreadyVerifiedException extends RuntimeException {
    public BusinessAlreadyVerifiedException(String message) {
        super(message);
    }
}
