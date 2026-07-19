package com.kaldar.kaldar.order.application.event;

import org.springframework.context.ApplicationEvent;

public class OrderAcceptedEvent extends ApplicationEvent {
    private final Long orderId;

    public OrderAcceptedEvent(Object source, Long orderId) {
        super(source);
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }
}
