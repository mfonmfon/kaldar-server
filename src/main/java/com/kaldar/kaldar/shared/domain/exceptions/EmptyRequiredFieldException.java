package com.kaldar.kaldar.shared.domain.exceptions;

public class EmptyRequiredFieldException extends RuntimeException {
    public EmptyRequiredFieldException(String message) {
        super(message);
    }
}
