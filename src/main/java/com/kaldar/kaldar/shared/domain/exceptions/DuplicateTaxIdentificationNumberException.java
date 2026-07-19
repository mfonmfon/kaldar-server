package com.kaldar.kaldar.shared.domain.exceptions;

public class DuplicateTaxIdentificationNumberException extends RuntimeException {
    public DuplicateTaxIdentificationNumberException(String message) {
        super(message);
    }
}
