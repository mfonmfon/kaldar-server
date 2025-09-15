package com.kaldar.kaldar.exceptions;

import org.springframework.http.HttpStatus;

public class PasswordMismatchException extends KaldarBusinessException {
    public PasswordMismatchException(String message) {
        super("PASSWORD_MIS_MATCH", message, HttpStatus.BAD_REQUEST);
    }
}
