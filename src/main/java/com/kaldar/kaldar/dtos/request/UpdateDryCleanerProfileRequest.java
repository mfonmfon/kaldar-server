package com.kaldar.kaldar.dtos.request;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class UpdateDryCleanerProfileRequest {
    private Long dryCleanerId;
    @NotBlank(message = "FirstName is required")
    private String firstName;
    @NotBlank(message = "LastName is required")
    private String lastName;
    @NotBlank(message = "BusinessName is required")
    private String businessName;
    @NotBlank(message = "ShopAddress is required")
    private String shopAddress;
    @NotBlank(message = "Business phoneNumber is required")
    private String businessPhoneNumber;
    private LocalDateTime updatedAt;


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getDryCleanerId() {
        return dryCleanerId;
    }

    public void setDryCleanerId(Long dryCleanerId) {
        this.dryCleanerId = dryCleanerId;
    }

    public @NotBlank(message = "FirstName is required") String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank(message = "FirstName is required") String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank(message = "BusinessName is required") String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(@NotBlank(message = "BusinessName is required") String businessName) {
        this.businessName = businessName;
    }

    public @NotBlank(message = "LastName is required") String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank(message = "LastName is required") String lastName) {
        this.lastName = lastName;
    }

    public @NotBlank(message = "ShopAddress is required") String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(@NotBlank(message = "ShopAddress is required") String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public @NotBlank(message = "Business phoneNumber is required") String getBusinessPhoneNumber() {
        return businessPhoneNumber;
    }

    public void setBusinessPhoneNumber(@NotBlank(message = "Business phoneNumber is required") String businessPhoneNumber) {
        this.businessPhoneNumber = businessPhoneNumber;
    }
}
