package com.kaldar.kaldar.domain.entities;

import jakarta.persistence.Entity;

@Entity
public class DispatchRider extends UserEntity {
    private boolean available = true;
    private Double currentLatitude;
    private Double currentLongtitude;
    private String vehicleType;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongtitude() {
        return currentLongtitude;
    }

    public void setCurrentLongtitude(Double currentLongtitude) {
        this.currentLongtitude = currentLongtitude;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}
