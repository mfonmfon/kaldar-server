package com.kaldar.kaldar.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class CustomerEntity extends UserEntity{

    private String defaultAddress;

    @OneToMany(mappedBy = "")
    private List<OrderEntity> orderEntityList;

    public List<OrderEntity> getOrderEntityList() {
        return orderEntityList;
    }

    public void setOrderEntityList(List<OrderEntity> orderEntityList) {
        this.orderEntityList = orderEntityList;
    }

    public String getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(String defaultPickupAddress) {
        this.defaultAddress = defaultPickupAddress;
    }



}
