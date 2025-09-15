package com.kaldar.kaldar.exceptions;

import org.springframework.http.HttpStatus;

public class KaldarBusinessException extends RuntimeException{
    private final String errorCode;
    private final HttpStatus httpStatus;

    public KaldarBusinessException(String errorCode, String message, HttpStatus httpStatus){
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
