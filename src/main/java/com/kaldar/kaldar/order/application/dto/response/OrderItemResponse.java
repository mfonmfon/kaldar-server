package com.kaldar.kaldar.order.application.dto.response;

public class OrderItemResponse {
    private String clothType;
    private int quantity;
    private double pricePerItem;
    private double subtotal;
    private String specialInstructions;

    // Getters and Setters
    public String getClothType() { return clothType; }
    public void setClothType(String clothType) { this.clothType = clothType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPricePerItem() { return pricePerItem; }
    public void setPricePerItem(double pricePerItem) { this.pricePerItem = pricePerItem; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
}
