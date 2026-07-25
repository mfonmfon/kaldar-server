package com.kaldar.kaldar.notification.application.dto.request;

public class ToggleReadRequest {

    private boolean isRead;

    public ToggleReadRequest() {}

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
