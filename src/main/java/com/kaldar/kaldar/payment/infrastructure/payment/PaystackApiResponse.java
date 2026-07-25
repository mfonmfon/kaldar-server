package com.kaldar.kaldar.payment.infrastructure.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Top-level wrapper returned by every Paystack REST API call.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackApiResponse {

    private boolean status;
    private String message;
    private PaystackInitData data;

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public PaystackInitData getData() { return data; }
    public void setData(PaystackInitData data) { this.data = data; }
}
