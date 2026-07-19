package com.kaldar.kaldar.order.application.dto.request;

public class SubmitReviewRequest {
    private Long orderId;
    private Long customerId;
    private Long dryCleanerId;
    private int rating;
    private String comment;

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getDryCleanerId() { return dryCleanerId; }
    public void setDryCleanerId(Long dryCleanerId) { this.dryCleanerId = dryCleanerId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
