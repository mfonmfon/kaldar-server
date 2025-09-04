package com.kaldar.kaldar.dtos.request;

public class VerifyOtpRequest {
    private String email;
    private String otpInput;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpInput() {
        return otpInput;
    }

    public void setOtpInput(String otpInput) {
        this.otpInput = otpInput;
    }
}
