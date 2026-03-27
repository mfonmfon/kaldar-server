package com.kaldar.kaldar.shared.infrastructure.auth.dto.request;

public class SendVerificationEmailRequest {
    private String recipientEmail;
    private String otpNumber;

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getOtpNumber() {
        return otpNumber;
    }

    public void setOtpNumber(String otpNumber) {
        this.otpNumber = otpNumber;
    }
}
