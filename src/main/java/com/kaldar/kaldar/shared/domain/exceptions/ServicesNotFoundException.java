package com.kaldar.kaldar.shared.domain.exceptions;

import org.springframework.http.HttpStatus;

public class ServicesNotFoundException extends RuntimeException{
    public ServicesNotFoundException(String message) {
        super(message);
    }
}
