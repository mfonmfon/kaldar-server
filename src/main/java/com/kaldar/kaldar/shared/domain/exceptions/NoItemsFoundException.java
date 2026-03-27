package com.kaldar.kaldar.shared.domain.exceptions;

public class NoItemsFoundException extends RuntimeException {
    public NoItemsFoundException(String message){
        super(message);
    }
}
