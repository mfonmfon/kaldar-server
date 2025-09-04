package com.kaldar.kaldar.dtos.response;

import java.time.LocalDateTime;

public class VerifyOtpResponse {
    private String email;
    private String verifiedAt;
    private String otpVerificationMessage;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(String verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getOtpVerificationMessage() {
        return otpVerificationMessage;
    }

    public void setOtpVerificationMessage(String otpVerificationMessage) {
        this.otpVerificationMessage = otpVerificationMessage;
    }
}
