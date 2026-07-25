package com.kaldar.kaldar.notification.application.dto.response;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private String title;
    private String message;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String metadata;

    public NotificationResponse() {}

    public NotificationResponse(Long id, String title, String message, String type,
                                 boolean isRead, LocalDateTime createdAt, String metadata) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.metadata = metadata;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}
