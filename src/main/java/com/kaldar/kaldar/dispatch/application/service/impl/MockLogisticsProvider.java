package com.kaldar.kaldar.dispatch.application.service.impl;

import com.kaldar.kaldar.dispatch.application.dto.LogisticsDeliveryRequest;
import com.kaldar.kaldar.dispatch.application.dto.LogisticsDeliveryResponse;
import com.kaldar.kaldar.dispatch.application.service.LogisticsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockLogisticsProvider implements LogisticsProvider {
    private static final Logger log = LoggerFactory.getLogger(MockLogisticsProvider.class);

    @Override
    public String getProviderName() {
        return "MOCK";
    }

    @Override
    public LogisticsDeliveryResponse requestDelivery(LogisticsDeliveryRequest request) {
        log.info("Mocking delivery request to provider. Type: {}, Order ID: {}, Pickup: {}, Dropoff: {}", 
                 request.getType(), request.getOrderId(), request.getPickupAddress(), request.getDropoffAddress());
        
        String externalId = "mock-del-" + UUID.randomUUID().toString().substring(0, 8);
        String trackingUrl = "https://mocklogistics.com/track/" + externalId;
        
        return new LogisticsDeliveryResponse(externalId, "REQUESTED", trackingUrl, "1500.00");
    }

    @Override
    public void cancelDelivery(String externalDeliveryId) {
        log.info("Mocking delivery cancellation for external ID: {}", externalDeliveryId);
    }
}
