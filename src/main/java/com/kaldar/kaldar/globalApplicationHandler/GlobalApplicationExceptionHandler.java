package com.kaldar.kaldar.globalApplicationHandler;

import com.kaldar.kaldar.dtos.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.info.InfoContributor;
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



    private ApiErrorResponse buildErrorResponse(String message, HttpStatus status, String path, Object details){
        return new ApiErrorResponse(
                message,
                status.getReasonPhrase(),
                status.value(),
                path,
                details,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request){
        Map<String , String> fieldsErrorMapper = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                fieldsErrorMapper.put(error.getField(), error.getDefaultMessage()));
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "Invalid data",
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                fieldsErrorMapper
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception exception, HttpServletRequest request){
        ApiErrorResponse apiErrorResponse = buildErrorResponse(
                "An unexpected error occurred ",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(),
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
    }



}
