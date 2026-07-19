package com.kaldar.kaldar.shared.api.handler;

import com.kaldar.kaldar.shared.api.response.ApiErrorResponse;
import com.kaldar.kaldar.shared.domain.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalApplicationExceptionHandler {
    private ApiErrorResponse buildErrorResponse(String message, HttpStatus status, String path, Object details) {
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
    @ResponseStatus
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {
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

    @ResponseStatus
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException exception,
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
    @ResponseStatus
    @ExceptionHandler({
            CustomerEmailAlreadyExist.class,
            DryCleanerEmailAlreadyExistException.class,
            DryCleanerBusinessEmailExistException.class,
            DuplicateRegistrationNumberException.class,
            DuplicateTaxIdentificationNumberException.class,
            BusinessAlreadyVerifiedException.class
    })
    public ResponseEntity<ApiErrorResponse> handleEmailConflictExceptions(RuntimeException exception, HttpServletRequest request) {
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
    @ResponseStatus
    @ExceptionHandler({
            OTPNotFoundException.class,
            ExpiredOtpException.class,
            InvalidOtpException.class,
            ResetTokenNotFoundException.class,
            ResetTokenExpiredException.class,
            InvalidResetTokenException.class
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
     * Handle business logic validation issues.
     */
    @ResponseStatus
    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBusinessLogicExceptions(RuntimeException exception, HttpServletRequest request) {
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

    /**
     * Handle file upload failures (wrong file type, Cloudinary I/O error, etc.).
     */
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ApiErrorResponse> handleFileUploadException(
            FileUploadException exception, HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                exception.getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    /**
     * Handle files larger than the configured multipart limit (10 MB).
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException exception, HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "File size exceeds the maximum allowed limit of 10 MB",
                HttpStatus.PAYLOAD_TOO_LARGE,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(apiErrorResponse);
    }
}