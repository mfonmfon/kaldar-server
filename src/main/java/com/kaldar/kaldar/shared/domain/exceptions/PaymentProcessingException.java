package com.kaldar.kaldar.shared.domain.exceptions;

import org.springframework.http.HttpStatus;

public class PaymentProcessingException extends KaldarBusinessException {
    public PaymentProcessingException(String message) {
        super("PAYMENT_PROCESSING_ERROR", message, HttpStatus.BAD_REQUEST);
    }
}
