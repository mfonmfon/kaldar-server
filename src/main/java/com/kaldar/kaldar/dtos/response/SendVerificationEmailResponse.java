package com.kaldar.kaldar.dtos.response;

public class SendVerificationEmailResponse {
    private String email;
    private String verificationMessage;
    private String expiresAt;


    public SendVerificationEmailResponse(String email, String verificationMessage, String expiresAt) {
        this.email = email;
        this.verificationMessage = verificationMessage;
        this.expiresAt = expiresAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationMessage() {
        return verificationMessage;
    }

    public void setVerificationMessage(String verificationMessage) {
        this.verificationMessage = verificationMessage;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
}
