package com.kaldar.kaldar.domain.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private OrderEntity order;
    @ManyToOne
    private ServiceOfferings serviceOfferings;
    private String clothType;
    private int quantity;
    private Double lineTotal;




    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClothType() {
        return clothType;
    }

    public void setClothType(String clothType) {
        this.clothType = clothType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(Double lineTotal) {
        this.lineTotal = lineTotal;
    }

    public ServiceOfferings getServiceOfferings() {
        return serviceOfferings;
    }

    public void setServiceOfferings(ServiceOfferings serviceOfferings) {
        this.serviceOfferings = serviceOfferings;
    }
}
