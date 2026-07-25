package com.kaldar.kaldar.shared.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidWalletAmountException extends KaldarBusinessException {
    public InvalidWalletAmountException(String message) {
        super("INVALID_WALLET_AMOUNT", message, HttpStatus.BAD_REQUEST);
    }
}
