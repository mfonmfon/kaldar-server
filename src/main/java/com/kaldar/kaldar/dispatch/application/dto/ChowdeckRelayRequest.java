package com.kaldar.kaldar.dispatch.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChowdeckRelayRequest {
    @JsonProperty("reference")
    private String reference;

    @JsonProperty("pickup")
    private LocationDetails pickup;

    @JsonProperty("dropoff")
    private LocationDetails dropoff;

    public ChowdeckRelayRequest() {}

    public ChowdeckRelayRequest(String reference, LocationDetails pickup, LocationDetails dropoff) {
        this.reference = reference;
        this.pickup = pickup;
        this.dropoff = dropoff;
    }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public LocationDetails getPickup() { return pickup; }
    public void setPickup(LocationDetails pickup) { this.pickup = pickup; }

    public LocationDetails getDropoff() { return dropoff; }
    public void setDropoff(LocationDetails dropoff) { this.dropoff = dropoff; }
}
