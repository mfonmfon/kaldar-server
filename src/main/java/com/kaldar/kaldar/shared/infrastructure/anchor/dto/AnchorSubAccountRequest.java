package com.kaldar.kaldar.shared.infrastructure.anchor.dto;

public class AnchorSubAccountRequest {

    private final String fullName;
    private final String email;
    private final String phoneNumber;

    public AnchorSubAccountRequest(String fullName, String email, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
}
