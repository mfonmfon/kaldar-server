package com.kaldar.kaldar.dispatch.domain.model;

import com.kaldar.kaldar.order.domain.model.OrderEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_tasks")
public class DeliveryTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(nullable = false)
    private String provider; // e.g. "CHOWDECK", "UBER"

    private String externalDeliveryId; // The ID returned by Chowdeck or Uber

    @Column(nullable = false)
    private String status; // REQUESTED, ASSIGNED, PICKED_UP, DELIVERED, CANCELLED

    private String trackingUrl;

    private String riderName;
    private String riderPhone;

    private String type; // PICKUP or DELIVERY

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getExternalDeliveryId() { return externalDeliveryId; }
    public void setExternalDeliveryId(String externalDeliveryId) { this.externalDeliveryId = externalDeliveryId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTrackingUrl() { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }

    public String getRiderName() { return riderName; }
    public void setRiderName(String riderName) { this.riderName = riderName; }

    public String getRiderPhone() { return riderPhone; }
    public void setRiderPhone(String riderPhone) { this.riderPhone = riderPhone; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
