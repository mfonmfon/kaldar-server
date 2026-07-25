package com.kaldar.kaldar.shared.domain.exceptions;

import org.springframework.http.HttpStatus;

public class PayoutAccountNotConfiguredException extends KaldarBusinessException {
    public PayoutAccountNotConfiguredException(String message) {
        super("PAYOUT_ACCOUNT_NOT_CONFIGURED", message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
