package com.kaldar.kaldar.shared.domain.exceptions;

import org.springframework.http.HttpStatus;

public class NoItemsFoundException extends KaldarBusinessException {
    public NoItemsFoundException(String message) {
        super("NO_ITEMS_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
