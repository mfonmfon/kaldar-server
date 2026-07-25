package com.kaldar.kaldar.payment.infrastructure.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Maps the {@code data} object returned by Paystack's
 * {@code POST /transaction/initialize} endpoint.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackInitData {

    @JsonProperty("authorization_url")
    private String authorizationUrl;

    @JsonProperty("access_code")
    private String accessCode;

    private String reference;

    public String getAuthorizationUrl() { return authorizationUrl; }
    public void setAuthorizationUrl(String authorizationUrl) { this.authorizationUrl = authorizationUrl; }

    public String getAccessCode() { return accessCode; }
    public void setAccessCode(String accessCode) { this.accessCode = accessCode; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}
