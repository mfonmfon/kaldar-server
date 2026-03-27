package com.kaldar.kaldar.shared.domain.exceptions;

public class CustomerEmailAlreadyExist extends RuntimeException {
    public CustomerEmailAlreadyExist(String message) {
        super(message);
    }
}
