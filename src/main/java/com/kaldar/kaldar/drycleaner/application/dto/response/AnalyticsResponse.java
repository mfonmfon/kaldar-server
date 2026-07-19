package com.kaldar.kaldar.drycleaner.application.dto.response;

import java.math.BigDecimal;

public class AnalyticsResponse {
    private BigDecimal revenue;
    private String revenueChange;
    private Long orders;
    private String ordersChange;
    private Double avgRating;
    private String avgRatingChange;

    public AnalyticsResponse() {}

    public AnalyticsResponse(BigDecimal revenue, String revenueChange, Long orders, String ordersChange, Double avgRating, String avgRatingChange) {
        this.revenue = revenue;
        this.revenueChange = revenueChange;
        this.orders = orders;
        this.ordersChange = ordersChange;
        this.avgRating = avgRating;
        this.avgRatingChange = avgRatingChange;
    }

    // Getters and Setters
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
    public String getRevenueChange() { return revenueChange; }
    public void setRevenueChange(String revenueChange) { this.revenueChange = revenueChange; }
    public Long getOrders() { return orders; }
    public void setOrders(Long orders) { this.orders = orders; }
    public String getOrdersChange() { return ordersChange; }
    public void setOrdersChange(String ordersChange) { this.ordersChange = ordersChange; }
    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
    public String getAvgRatingChange() { return avgRatingChange; }
    public void setAvgRatingChange(String avgRatingChange) { this.avgRatingChange = avgRatingChange; }
}
