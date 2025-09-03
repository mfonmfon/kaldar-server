package com.kaldar.kaldar.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DryCleanerRegistrationRequest {
    @NotBlank(message = "FirstName is required")
    private String firstName;

    @NotBlank(message = "LastName is required")
    private String lastName;

    @Email(message = "Email cannot be invalid")
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "BusinessName is required")
    private String businessName;

    @Email(message = "Cannot be an invalid Email")
    @NotBlank(message = "BusinessAddress is required")
    private String businessEmail;

    @NotBlank(message = "ShopAddress is required")
    private String shopAddress;

    @NotBlank(message = "Business phoneNumber is required")
    private String businessPhoneNumber;

    @Size(min = 8 , message = "Password must be at least 10 characters long")
    @NotBlank(message = "Password is required")
    private String password;


    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getBusinessPhoneNumber() {
        return businessPhoneNumber;
    }

    public void setBusinessPhoneNumber(String businessPhoneNumber) {
        this.businessPhoneNumber = businessPhoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
