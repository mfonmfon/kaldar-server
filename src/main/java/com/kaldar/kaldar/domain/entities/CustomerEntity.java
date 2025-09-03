package com.kaldar.kaldar.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class CustomerEntity extends UserEntity{

    private String defaultAddress;


    public String getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(String defaultPickupAddress) {
        this.defaultAddress = defaultPickupAddress;
    }



}
