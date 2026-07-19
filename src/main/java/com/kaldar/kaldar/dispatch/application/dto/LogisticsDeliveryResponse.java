package com.kaldar.kaldar.dispatch.application.dto;

public class LogisticsDeliveryResponse {
    private String externalDeliveryId;
    private String status;
    private String trackingUrl;
    private String fee;

    public LogisticsDeliveryResponse() {}

    public LogisticsDeliveryResponse(String externalDeliveryId, String status, String trackingUrl, String fee) {
        this.externalDeliveryId = externalDeliveryId;
        this.status = status;
        this.trackingUrl = trackingUrl;
        this.fee = fee;
    }

    public String getExternalDeliveryId() { return externalDeliveryId; }
    public void setExternalDeliveryId(String externalDeliveryId) { this.externalDeliveryId = externalDeliveryId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTrackingUrl() { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }

    public String getFee() { return fee; }
    public void setFee(String fee) { this.fee = fee; }
}
