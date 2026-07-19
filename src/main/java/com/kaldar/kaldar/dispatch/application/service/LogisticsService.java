package com.kaldar.kaldar.dispatch.application.service;

import com.kaldar.kaldar.dispatch.domain.model.DeliveryTask;
import java.util.Map;

public interface LogisticsService {
    DeliveryTask requestPickupDelivery(Long orderId);
    void handleWebhookCallback(String provider, Map<String, Object> payload);
}
