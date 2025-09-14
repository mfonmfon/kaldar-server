package com.kaldar.kaldar.dtos.request;

import com.kaldar.kaldar.domain.entities.ServiceOfferings;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CreateOrderRequest {
    private Long customerId;
    private Long dryCleanerId;
    private String pickupAddress;
    private String deliveryAddress;
    private List<OrderItemsRequest> orderItemsRequests;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getDryCleanerId() {
        return dryCleanerId;
    }

    public void setDryCleanerId(Long dryCleanerId) {
        this.dryCleanerId = dryCleanerId;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<OrderItemsRequest> getOrderItemsRequests() {
        return orderItemsRequests;
    }

    public void setOrderItemsRequests(List<OrderItemsRequest> orderItemsRequests) {
        this.orderItemsRequests = orderItemsRequests;
    }
}
