package com.kaldar.kaldar.dispatch.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationDetails {
    @JsonProperty("address")
    private String address;

    @JsonProperty("name")
    private String name;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("notes")
    private String notes;

    public LocationDetails() {}

    public LocationDetails(String address, String name, String phone, String notes) {
        this.address = address;
        this.name = name;
        this.phone = phone;
        this.notes = notes;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
