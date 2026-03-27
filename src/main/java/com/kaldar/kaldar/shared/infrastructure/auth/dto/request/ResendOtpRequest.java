package com.kaldar.kaldar.shared.infrastructure.auth.dto.request;

public class ResendOtpRequest {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
