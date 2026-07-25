package com.kaldar.kaldar.notification.application.dto.request;

import java.util.List;

public class BulkNotificationRequest {

    private List<Long> ids;

    public BulkNotificationRequest() {}

    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }
}
