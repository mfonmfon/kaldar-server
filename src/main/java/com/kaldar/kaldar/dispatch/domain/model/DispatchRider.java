package com.kaldar.kaldar.dispatch.domain.model;

import com.kaldar.kaldar.shared.domain.model.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
