package com.kaldar.kaldar.shared.domain.exceptions;

public class OrdersNotFoundException extends RuntimeException {
    public OrdersNotFoundException(String message) {
        super(message);
    }
}
