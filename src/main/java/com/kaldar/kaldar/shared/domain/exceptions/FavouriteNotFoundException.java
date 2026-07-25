package com.kaldar.kaldar.shared.domain.exceptions;

import org.springframework.http.HttpStatus;

public class FavouriteNotFoundException extends KaldarBusinessException {
    public FavouriteNotFoundException(String message) {
        super("FAVOURITE_NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
