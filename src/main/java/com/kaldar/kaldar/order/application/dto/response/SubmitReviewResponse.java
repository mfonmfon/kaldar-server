package com.kaldar.kaldar.order.application.dto.response;

public class SubmitReviewResponse {
    private Long reviewId;
    private String message;

    public SubmitReviewResponse() {}
    public SubmitReviewResponse(Long reviewId, String message) {
        this.reviewId = reviewId;
        this.message = message;
    }

    // Getters and Setters
    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
