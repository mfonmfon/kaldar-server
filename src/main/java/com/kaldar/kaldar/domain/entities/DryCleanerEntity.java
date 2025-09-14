package com.kaldar.kaldar.domain.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
public class DryCleanerEntity extends UserEntity{

    @NotBlank(message = "Business name is required")
    private String businessName;
    @NotBlank(message = "Business address is required")
    private String businessAddress;
    private String workingHours;
    @OneToMany(mappedBy = "dryCleaner")
    private List<OrderEntity> orderEntityList;
    @OneToMany(mappedBy = "dryCleaner")
    private List<ServiceOfferings> serviceOfferings;
    private boolean isActive;


    public @NotBlank(message = "Business name is required") String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(@NotBlank(message = "Business name is required") String businessName) {
        this.businessName = businessName;
    }

    public @NotBlank(message = "Business address is required") String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(@NotBlank(message = "Business address is required") String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public List<OrderEntity> getOrderEntityList() {
        return orderEntityList;
    }

    public void setOrderEntityList(List<OrderEntity> orderEntityList) {
        this.orderEntityList = orderEntityList;
    }

    public List<ServiceOfferings> getServiceOfferings() {
        return serviceOfferings;
    }

    public void setServiceOfferings(List<ServiceOfferings> serviceOfferings) {
        this.serviceOfferings = serviceOfferings;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
