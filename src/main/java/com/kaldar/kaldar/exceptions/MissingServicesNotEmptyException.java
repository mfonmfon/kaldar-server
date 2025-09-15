package com.kaldar.kaldar.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public class MissingServicesNotEmptyException extends KaldarBusinessException {
    public MissingServicesNotEmptyException(List<String> missingServices) {
        super("MISSING_SERVICE", "Order can not be accepted; service missing" + String.join(", " , missingServices),
                HttpStatus.BAD_REQUEST);

    }
}
