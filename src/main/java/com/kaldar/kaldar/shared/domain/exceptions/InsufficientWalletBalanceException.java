package com.kaldar.kaldar.shared.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InsufficientWalletBalanceException extends KaldarBusinessException {
    public InsufficientWalletBalanceException(String message) {
        super("INSUFFICIENT_WALLET_BALANCE", message, HttpStatus.BAD_REQUEST);
    }
}
