package com.kaldar.kaldar.dtos.response;

public class SendVerificationEmailResponse {
    private String email;
    private String verificationToken;
    private String expiresAt;


    public SendVerificationEmailResponse(String email, String verificationToken, String expiresAt) {
        this.email = email;
        this.verificationToken = verificationToken;
        this.expiresAt = expiresAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
}
