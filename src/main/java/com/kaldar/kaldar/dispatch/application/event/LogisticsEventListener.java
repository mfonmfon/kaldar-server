package com.kaldar.kaldar.dispatch.application.event;

import com.kaldar.kaldar.dispatch.application.service.LogisticsService;
import com.kaldar.kaldar.order.application.event.OrderAcceptedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class LogisticsEventListener {
    private static final Logger log = LoggerFactory.getLogger(LogisticsEventListener.class);

    private final LogisticsService logisticsService;

    public LogisticsEventListener(LogisticsService logisticsService) {
        this.logisticsService = logisticsService;
    }

    @Async
    @EventListener
    public void handleOrderAccepted(OrderAcceptedEvent event) {
        log.info("Received OrderAcceptedEvent for order ID: {}. Triggering logistics booking...", event.getOrderId());
        try {
            logisticsService.requestPickupDelivery(event.getOrderId());
            log.info("Logistics pickup delivery successfully requested for order ID: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to automatically request logistics pickup for order ID {}: {}", event.getOrderId(), e.getMessage(), e);
        }
    }
}
