package com.kaldar.kaldar.order.application.dto.response;

import java.time.LocalDateTime;

public class RejectOrderResponse {
    private Long orderId;
    private String status;
    private LocalDateTime timestamp;

    public RejectOrderResponse(Long orderId, String status, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
