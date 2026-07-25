package com.kaldar.kaldar.shared.domain.exceptions;

import org.springframework.http.HttpStatus;

public class OrdersNotFoundException extends KaldarBusinessException {
    public OrdersNotFoundException(String message) {
        super("ORDERS_NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
