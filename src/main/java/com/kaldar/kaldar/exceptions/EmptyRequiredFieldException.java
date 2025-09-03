package com.kaldar.kaldar.exceptions;

public class EmptyRequiredFieldException extends RuntimeException {
    public EmptyRequiredFieldException(String message) {
        super(message);
    }
}
