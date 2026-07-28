package com.kaldar.kaldar.shared.api.handler;

import com.kaldar.kaldar.shared.api.response.ApiErrorResponse;
import com.kaldar.kaldar.shared.domain.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Production-grade Global Exception Handler.
 *
 * <p>Ensures that every exception maps to an appropriate HTTP status code
 * (400, 401, 403, 404, 405, 409, 415, 422) with clean, user-friendly error messages
 * ready for direct display in the frontend UI.</p>
 */
@RestControllerAdvice
public class GlobalApplicationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalApplicationExceptionHandler.class);

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

    // =========================================================================
    // 1. KALDAR BUSINESS EXCEPTION HIERARCHY (400, 404, 409, 422, etc.)
    // =========================================================================

    @ExceptionHandler(KaldarBusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleKaldarBusinessException(
            KaldarBusinessException exception, HttpServletRequest request) {
        log.warn("Business exception on [{} {}]: {} ({})",
                request.getMethod(), request.getRequestURI(), exception.getMessage(), exception.getErrorCode());

        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                exception.getMessage(),
                exception.getHttpStatus(),
                request.getRequestURI(),
                Map.of("errorCode", exception.getErrorCode())
        );
        return ResponseEntity.status(exception.getHttpStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(
            UserNotFoundException exception, HttpServletRequest request) {
        log.warn("User not found on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "We couldn't find an account matching your request.",
                HttpStatus.NOT_FOUND,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }

    @ExceptionHandler({
            CustomerEmailAlreadyExist.class,
            DryCleanerEmailAlreadyExistException.class,
            DryCleanerBusinessEmailExistException.class
    })
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExistExceptions(
            RuntimeException exception, HttpServletRequest request) {
        log.warn("Email conflict on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "An account with this email address already exists. Please sign in instead.",
                HttpStatus.CONFLICT,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiErrorResponse);
    }

    @ExceptionHandler({
            DuplicateRegistrationNumberException.class,
            DuplicateTaxIdentificationNumberException.class,
            BusinessAlreadyVerifiedException.class
    })
    public ResponseEntity<ApiErrorResponse> handleConflictExceptions(
            RuntimeException exception, HttpServletRequest request) {
        log.warn("Conflict exception on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                exception.getMessage() != null ? exception.getMessage() : "A record with these business credentials already exists.",
                HttpStatus.CONFLICT,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiErrorResponse);
    }

    @ExceptionHandler({
            OTPNotFoundException.class,
            ExpiredOtpException.class,
            InvalidOtpException.class,
            ResetTokenNotFoundException.class,
            ResetTokenExpiredException.class,
            InvalidResetTokenException.class
    })
    public ResponseEntity<ApiErrorResponse> handleOtpExceptions(
            RuntimeException exception, HttpServletRequest request) {
        log.warn("OTP/Token exception on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                exception.getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    // =========================================================================
    // 2. SPRING SECURITY & AUTHENTICATION EXCEPTIONS (401, 403)
    // =========================================================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException exception, HttpServletRequest request) {
        log.warn("Bad credentials on [{} {}]", request.getMethod(), request.getRequestURI());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "Invalid email or password. Please check your login details and try again.",
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiErrorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException exception, HttpServletRequest request) {
        log.warn("Access denied on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "Access denied. You do not have permission to perform this action.",
                HttpStatus.FORBIDDEN,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiErrorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            AuthenticationException exception, HttpServletRequest request) {
        log.warn("Authentication failed on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "Your session has expired or authentication failed. Please log in again.",
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiErrorResponse);
    }

    // =========================================================================
    // 3. HTTP REQUEST & VALIDATION EXCEPTIONS (400, 405, 409, 413)
    // =========================================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        log.warn("Validation failed on [{} {}]: {}", request.getMethod(), request.getRequestURI(), fieldErrors);

        String primaryErrorMessage = fieldErrors.values().stream().findFirst()
                .orElse("Please fill in all required fields correctly.");

        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                primaryErrorMessage,
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception, HttpServletRequest request) {
        log.warn("Constraint violation on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "Validation failed. Please check your inputs and try again.",
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException exception, HttpServletRequest request) {
        log.warn("Malformed JSON on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "Invalid request format. Please check submitted data.",
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
        log.warn("Method not supported on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "The requested operation is not supported.",
                HttpStatus.METHOD_NOT_ALLOWED,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(apiErrorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        String detail = String.format("Parameter '%s' should be of type '%s'",
                exception.getName(),
                exception.getRequiredType() != null ? exception.getRequiredType().getSimpleName() : "unknown");

        log.warn("Type mismatch on [{} {}]: {}", request.getMethod(), request.getRequestURI(), detail);
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "Invalid request parameter format.",
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                detail
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException exception, HttpServletRequest request) {
        log.warn("Missing parameter on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "Required parameter '" + exception.getParameterName() + "' is missing.",
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingHeader(
            MissingRequestHeaderException exception, HttpServletRequest request) {
        log.warn("Missing header on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "Required request header '" + exception.getHeaderName() + "' is missing.",
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException exception, HttpServletRequest request) {
        log.warn("Endpoint not found: [{} {}]", request.getMethod(), request.getRequestURI());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "The requested page or endpoint could not be found.",
                HttpStatus.NOT_FOUND,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException exception, HttpServletRequest request) {
        log.error("Data integrity violation on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "Record conflict: A record with these details already exists.",
                HttpStatus.CONFLICT,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiErrorResponse);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBusinessLogicExceptions(
            RuntimeException exception, HttpServletRequest request) {
        log.warn("IllegalArgument/IllegalState on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                exception.getMessage() != null ? exception.getMessage() : "Invalid request parameters.",
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ApiErrorResponse> handleFileUploadException(
            FileUploadException exception, HttpServletRequest request) {
        log.warn("File upload error on [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                exception.getMessage() != null ? exception.getMessage() : "File upload failed. Please try again with a valid file.",
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException exception, HttpServletRequest request) {
        log.warn("File size limit exceeded on [{} {}]", request.getMethod(), request.getRequestURI());
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "The selected file is too large. Maximum allowed file size is 10 MB.",
                HttpStatus.PAYLOAD_TOO_LARGE,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(apiErrorResponse);
    }

    // =========================================================================
    // 4. UNHANDLED INTERNAL SERVER ERRORS (500)
    // =========================================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(
            Exception exception, HttpServletRequest request) {
        log.error("Unhandled internal error on [{} {}]", request.getMethod(), request.getRequestURI(), exception);

        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "An unexpected server error occurred. Please try again later or contact support.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
    }
}