package com.kaldar.kaldar.payment.application.dto.response;

public class PaymentInitResponse {

    private String authorizationUrl;
    private String reference;
    private String accessCode;

    public PaymentInitResponse() {}

    public PaymentInitResponse(String authorizationUrl, String reference, String accessCode) {
        this.authorizationUrl = authorizationUrl;
        this.reference = reference;
        this.accessCode = accessCode;
    }

    public String getAuthorizationUrl() { return authorizationUrl; }
    public void setAuthorizationUrl(String authorizationUrl) { this.authorizationUrl = authorizationUrl; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getAccessCode() { return accessCode; }
    public void setAccessCode(String accessCode) { this.accessCode = accessCode; }
}
