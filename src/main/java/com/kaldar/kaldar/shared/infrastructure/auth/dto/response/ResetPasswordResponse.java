package com.kaldar.kaldar.shared.infrastructure.auth.dto.response;

public class ResetPasswordResponse {
    private String message;
    private String confirmationUrl;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConfirmationUrl() {
        return confirmationUrl;
    }

    public void setConfirmationUrl(String confirmationUrl) {
        this.confirmationUrl = confirmationUrl;
    }
}

