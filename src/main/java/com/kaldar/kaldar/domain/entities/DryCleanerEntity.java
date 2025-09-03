package com.kaldar.kaldar.domain.entities;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

@Entity
public class DryCleanerEntity extends UserEntity{

    @NotBlank(message = "Business name is required")
    private String businessName;
    @NotBlank(message = "Business address is required")
    private String businessAddress;
    private String workingHours;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }
}
