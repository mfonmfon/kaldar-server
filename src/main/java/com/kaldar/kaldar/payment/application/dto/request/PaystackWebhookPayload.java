package com.kaldar.kaldar.payment.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Internal DTO that maps the Paystack webhook payload.
 * Only the fields we act on are mapped; {@code @JsonIgnoreProperties} silences
 * any extra fields Paystack adds in the future.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackWebhookPayload {

    private String event;
    private Data data;

    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private String reference;
        private String status;
        private BigDecimal amount; // Paystack returns amount in kobo
        private String channel;

        @JsonProperty("gateway_response")
        private String gatewayResponse;

        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getChannel() { return channel; }
        public void setChannel(String channel) { this.channel = channel; }

        public String getGatewayResponse() { return gatewayResponse; }
        public void setGatewayResponse(String gatewayResponse) { this.gatewayResponse = gatewayResponse; }
    }
}
