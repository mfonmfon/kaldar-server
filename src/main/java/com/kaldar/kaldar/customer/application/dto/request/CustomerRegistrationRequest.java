package com.kaldar.kaldar.customer.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CustomerRegistrationRequest {
    @Schema(description = "First Name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Firstname is required")
    private String firstName;

    @Schema(description = "Last Name", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Lastname is required")
    private String lastName;

    @Schema(description = "Email Address", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "Must be a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "Phone Number", example = "+2348012345678", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @Schema(description = "Account Password", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password is required")
    private String password;

    @Schema(description = "Default Address", example = "123 Main St, Lagos", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Address is required")
    private String address;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
