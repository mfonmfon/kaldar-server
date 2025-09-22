package com.kaldar.kaldar.dtos.request;
import com.kaldar.kaldar.domain.entities.ClothItems;

import java.time.LocalDateTime;
import java.util.List;

public class CreateOrderRequest {
    private Long customerId;
    private Long dryCleanerId;
    private String pickupAddress;
    private String deliveryAddress;
    private int quantity;
    private String washingPreference;
    private String orderStatus;
    private LocalDateTime createdAt;
    private List<ClothItems> clothes;

    public Long getDryCleanerId() {
        return dryCleanerId;
    }

    public void setDryCleanerId(Long dryCleanerId) {
        this.dryCleanerId = dryCleanerId;
    }

    public List<ClothItems> getClothes() {
        return clothes;
    }

    public void setClothes(List<ClothItems> clothes) {
        this.clothes = clothes;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getWashingPreference() {
        return washingPreference;
    }

    public void setWashingPreference(String washingPreference) {
        this.washingPreference = washingPreference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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

}
