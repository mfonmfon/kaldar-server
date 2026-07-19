package com.kaldar.kaldar.dispatch.application.service.impl;

import com.kaldar.kaldar.dispatch.application.dto.LogisticsDeliveryRequest;
import com.kaldar.kaldar.dispatch.application.dto.LogisticsDeliveryResponse;
import com.kaldar.kaldar.dispatch.application.service.LogisticsProvider;
import com.kaldar.kaldar.dispatch.application.service.LogisticsService;
import com.kaldar.kaldar.dispatch.domain.model.DeliveryTask;
import com.kaldar.kaldar.dispatch.domain.repository.DeliveryTaskRepository;
import com.kaldar.kaldar.order.domain.model.OrderEntity;
import com.kaldar.kaldar.order.domain.repository.OrderEntityRepository;
import com.kaldar.kaldar.shared.domain.constants.OrderStatus;
import com.kaldar.kaldar.shared.domain.exceptions.OrdersNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DefaultLogisticsService implements LogisticsService {
    private static final Logger log = LoggerFactory.getLogger(DefaultLogisticsService.class);

    private final DeliveryTaskRepository deliveryTaskRepository;
    private final OrderEntityRepository orderEntityRepository;
    private final List<LogisticsProvider> providers;
    private final com.kaldar.kaldar.wallet.application.service.WalletService walletService;

    @Value("${logistics.active-provider:MOCK}")
    private String activeProviderName;

    public DefaultLogisticsService(DeliveryTaskRepository deliveryTaskRepository,
                                   OrderEntityRepository orderEntityRepository,
                                   List<LogisticsProvider> providers,
                                   com.kaldar.kaldar.wallet.application.service.WalletService walletService) {
        this.deliveryTaskRepository = deliveryTaskRepository;
        this.orderEntityRepository = orderEntityRepository;
        this.providers = providers;
        this.walletService = walletService;
    }

    private LogisticsProvider getActiveProvider() {
        return providers.stream()
                .filter(p -> p.getProviderName().equalsIgnoreCase(activeProviderName))
                .findFirst()
                .orElseGet(() -> providers.stream()
                        .filter(p -> p.getProviderName().equalsIgnoreCase("MOCK"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No active logistics provider configured")));
    }

    @Override
    @Transactional
    public DeliveryTask requestPickupDelivery(Long orderId) {
        OrderEntity order = orderEntityRepository.findById(orderId)
                .orElseThrow(() -> new OrdersNotFoundException("Order not found with ID: " + orderId));

        LogisticsProvider provider = getActiveProvider();
        
        LogisticsDeliveryRequest request = new LogisticsDeliveryRequest();
        request.setOrderId(order.getId());
        request.setType("PICKUP");
        
        // Pickup details: Customer Info
        request.setPickupAddress(order.getPickupAddress());
        request.setPickupName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());
        request.setPickupPhone(order.getCustomer().getPhoneNumber());
        request.setPickupNotes(order.getWashingPreference());

        // Dropoff details: Dry Cleaner Info
        if (order.getDryCleaner() != null) {
            request.setDropoffAddress(order.getDryCleaner().getBusinessAddress());
            request.setDropoffName(order.getDryCleaner().getBusinessName());
            request.setDropoffPhone(order.getDryCleaner().getPhoneNumber());
        }

        LogisticsDeliveryResponse response = provider.requestDelivery(request);

        DeliveryTask task = new DeliveryTask();
        task.setOrder(order);
        task.setProvider(provider.getProviderName());
        task.setExternalDeliveryId(response.getExternalDeliveryId());
        task.setStatus(response.getStatus());
        task.setTrackingUrl(response.getTrackingUrl());
        task.setType("PICKUP");
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        return deliveryTaskRepository.save(task);
    }

    @Override
    @Transactional
    public void handleWebhookCallback(String provider, Map<String, Object> payload) {
        log.info("Received logistics webhook callback from provider: {}", provider);
        
        String externalId = null;
        String eventStatus = null;
        
        if ("CHOWDECK".equalsIgnoreCase(provider)) {
            // Chowdeck webhook payload mapping: { "category": "ORDER_ASSIGNED", "payload": { "id": 1453, ... } }
            String category = String.valueOf(payload.get("category"));
            Map data = (Map) payload.get("payload");
            if (data != null) {
                externalId = String.valueOf(data.get("id"));
                
                // Map Chowdeck events to our statuses
                if ("ORDER_ASSIGNED".equals(category) || "DISPATCHED".equals(category)) {
                    eventStatus = "ASSIGNED";
                } else if ("PICKED_UP".equals(category)) {
                    eventStatus = "PICKED_UP";
                } else if ("DELIVERED".equals(category)) {
                    eventStatus = "DELIVERED";
                } else if ("CANCELLED".equals(category)) {
                    eventStatus = "CANCELLED";
                }
            }
        }

        if (externalId == null || eventStatus == null) {
            log.warn("Invalid or unmapped webhook event received: {}", payload);
            return;
        }

        final String finalExternalId = externalId;
        final String finalEventStatus = eventStatus;

        // Find the matching delivery task
        deliveryTaskRepository.findByProviderAndExternalDeliveryId(provider.toUpperCase(), finalExternalId)
                .ifPresent(task -> {
                    task.setStatus(finalEventStatus);
                    task.setUpdatedAt(LocalDateTime.now());
                    deliveryTaskRepository.save(task);
                    
                    // Automatically update parent order status based on leg delivery status
                    OrderEntity order = task.getOrder();
                    if ("PICKED_UP".equals(finalEventStatus)) {
                        order.setOrderStatus(OrderStatus.PICKED); // Matches status machine logic
                    } else if ("DELIVERED".equals(finalEventStatus)) {
                        if ("PICKUP".equals(task.getType())) {
                            order.setOrderStatus(OrderStatus.CLEANING); // Clothes delivered to Dry Cleaner -> start cleaning
                            
                            // Calculate Dry Cleaner earnings (85% of total order value, e.g. 15% platform commission)
                            double totalAmount = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
                            double earnings = totalAmount * 0.85;
                            if (earnings > 0 && order.getDryCleaner() != null) {
                                walletService.creditWallet(new com.kaldar.kaldar.wallet.application.dto.WalletCreditRequest(
                                    order.getDryCleaner().getId(),
                                    java.math.BigDecimal.valueOf(earnings),
                                    "Earnings for Order #" + order.getId(),
                                    "ORD-" + order.getId()
                                ));
                                log.info("Credited Dry Cleaner ID {} wallet with ₦{} for Order #{}", 
                                         order.getDryCleaner().getId(), earnings, order.getId());
                            }
                        } else {
                            order.setOrderStatus(OrderStatus.DELIVERED); // Deliver leg complete
                        }
                    } else if ("CANCELLED".equals(finalEventStatus)) {
                        order.setOrderStatus(OrderStatus.CANCELLED);
                    }
                    orderEntityRepository.save(order);
                    log.info("Updated order ID {} status to {} via logistics webhook", order.getId(), order.getOrderStatus());
                });
    }
}
