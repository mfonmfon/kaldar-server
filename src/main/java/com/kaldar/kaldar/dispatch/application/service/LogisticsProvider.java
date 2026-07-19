package com.kaldar.kaldar.dispatch.application.service;

import com.kaldar.kaldar.dispatch.application.dto.LogisticsDeliveryRequest;
import com.kaldar.kaldar.dispatch.application.dto.LogisticsDeliveryResponse;

public interface LogisticsProvider {
    String getProviderName();
    LogisticsDeliveryResponse requestDelivery(LogisticsDeliveryRequest request);
    void cancelDelivery(String externalDeliveryId);
}
