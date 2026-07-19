package com.kaldar.kaldar.dispatch.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RelayData {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("status")
    private String status;

    @JsonProperty("delivery_price")
    private Double deliveryPrice;

    @JsonProperty("tracking_url")
    private String trackingUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getDeliveryPrice() { return deliveryPrice; }
    public void setDeliveryPrice(Double deliveryPrice) { this.deliveryPrice = deliveryPrice; }

    public String getTrackingUrl() { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }
}
