package com.kaldar.kaldar.dispatch.application.dto;

public class LogisticsDeliveryRequest {
    private Long orderId;
    
    // Pickup (Customer Details)
    private String pickupAddress;
    private String pickupName;
    private String pickupPhone;
    private String pickupNotes;

    // Dropoff (Drycleaner/Shop Details)
    private String dropoffAddress;
    private String dropoffName;
    private String dropoffPhone;
    private String dropoffNotes;

    private Double totalAmount;
    private String type; // PICKUP or DELIVERY

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getPickupName() { return pickupName; }
    public void setPickupName(String pickupName) { this.pickupName = pickupName; }

    public String getPickupPhone() { return pickupPhone; }
    public void setPickupPhone(String pickupPhone) { this.pickupPhone = pickupPhone; }

    public String getPickupNotes() { return pickupNotes; }
    public void setPickupNotes(String pickupNotes) { this.pickupNotes = pickupNotes; }

    public String getDropoffAddress() { return dropoffAddress; }
    public void setDropoffAddress(String dropoffAddress) { this.dropoffAddress = dropoffAddress; }

    public String getDropoffName() { return dropoffName; }
    public void setDropoffName(String dropoffName) { this.dropoffName = dropoffName; }

    public String getDropoffPhone() { return dropoffPhone; }
    public void setDropoffPhone(String dropoffPhone) { this.dropoffPhone = dropoffPhone; }

    public String getDropoffNotes() { return dropoffNotes; }
    public void setDropoffNotes(String dropoffNotes) { this.dropoffNotes = dropoffNotes; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
