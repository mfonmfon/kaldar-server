package com.kaldar.kaldar.exceptions;

public class CustomerEmailAlreadyExist extends RuntimeException {
    public CustomerEmailAlreadyExist(String message) {
        super(message);
    }
}
