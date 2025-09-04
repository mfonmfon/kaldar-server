package com.kaldar.kaldar.globalApplicationHandler;

import com.kaldar.kaldar.dtos.response.ApiErrorResponse;
import com.kaldar.kaldar.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalApplicationExceptionHandler {

    private ApiErrorResponse buildErrorResponse(
            String message,
            HttpStatus status,
            String path,
            Object details) {
        return new ApiErrorResponse(
                message,
                status.getReasonPhrase(),
                status.value(),
                path,
                details,
                LocalDateTime.now()
        );
    }

    /**
     * Handle validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "Invalid request data",
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    /**
     * Handle user not found.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(
            UserNotFoundException exception,
            HttpServletRequest request) {

        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }

    /**
     * Handle customer and drycleaner email conflicts.
     */
    @ExceptionHandler({
            CustomerEmailAlreadyExist.class,
            DryCleanerEmailAlreadyExistException.class,
            DryCleanerBusinessEmailExistException.class
    })
    public ResponseEntity<ApiErrorResponse> handleEmailConflictExceptions(
            RuntimeException exception,
            HttpServletRequest request) {

        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                exception.getMessage(),
                HttpStatus.CONFLICT,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiErrorResponse);
    }

    /**
     * Handle OTP-related exceptions.
     */
    @ExceptionHandler({
            OTPNotFoundException.class,
            ExpiredOtpException.class,
            InvalidOtpException.class
    })
    public ResponseEntity<ApiErrorResponse> handleOtpExceptions(RuntimeException exception, HttpServletRequest request) {

        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                exception.getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    /**
     * Catch-all for unhandled exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception exception, HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(),
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
    }
}
