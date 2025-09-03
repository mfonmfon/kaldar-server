package com.kaldar.kaldar.dtos.response;

import java.time.LocalDateTime;

public class ApiErrorResponse {
    private String message;
    private String error;
    private int status;
    private String path;
    private Object details;
    private LocalDateTime timestamp;

    public ApiErrorResponse(String message, String error, int status, String path, Object details, LocalDateTime timestamp) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.path = path;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
