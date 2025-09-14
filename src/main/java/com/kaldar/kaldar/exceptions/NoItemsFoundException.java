package com.kaldar.kaldar.exceptions;

public class NoItemsFoundException extends RuntimeException {
    public NoItemsFoundException(String message){
        super(message);
    }
}
