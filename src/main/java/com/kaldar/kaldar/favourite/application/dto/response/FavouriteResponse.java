package com.kaldar.kaldar.favourite.application.dto.response;

import java.time.LocalDateTime;

public class FavouriteResponse {

    private Long dryCleanerId;
    private LocalDateTime createdAt;

    public FavouriteResponse() {}

    public FavouriteResponse(Long dryCleanerId, LocalDateTime createdAt) {
        this.dryCleanerId = dryCleanerId;
        this.createdAt = createdAt;
    }

    public Long getDryCleanerId() { return dryCleanerId; }
    public void setDryCleanerId(Long dryCleanerId) { this.dryCleanerId = dryCleanerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
