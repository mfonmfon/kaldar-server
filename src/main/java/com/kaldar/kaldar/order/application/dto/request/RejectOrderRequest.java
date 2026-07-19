package com.kaldar.kaldar.order.application.dto.request;

public class RejectOrderRequest {
    private Long orderId;
    private Long dryCleanerId;
    private String reason;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getDryCleanerId() {
        return dryCleanerId;
    }

    public void setDryCleanerId(Long dryCleanerId) {
        this.dryCleanerId = dryCleanerId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
