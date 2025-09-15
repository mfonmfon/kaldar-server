package com.kaldar.kaldar.dtos.request;

import java.time.LocalDateTime;

public class AcceptOrderResponse {
    private Long orderId;
    private String status;
    private LocalDateTime timestamp;


    public Long getOrderId() {

        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
