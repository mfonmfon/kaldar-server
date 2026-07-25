package com.kaldar.kaldar.shared.domain.exceptions;

import org.springframework.http.HttpStatus;

public class EmptyRequiredFieldException extends KaldarBusinessException {
    public EmptyRequiredFieldException(String message) {
        super("EMPTY_REQUIRED_FIELD", message, HttpStatus.BAD_REQUEST);
    }
}
