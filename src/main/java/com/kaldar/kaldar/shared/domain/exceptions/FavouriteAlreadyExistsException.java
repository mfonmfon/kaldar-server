package com.kaldar.kaldar.shared.domain.exceptions;

import org.springframework.http.HttpStatus;

public class FavouriteAlreadyExistsException extends KaldarBusinessException {
    public FavouriteAlreadyExistsException(String message) {
        super("FAVOURITE_ALREADY_EXISTS", message, HttpStatus.CONFLICT);
    }
}
