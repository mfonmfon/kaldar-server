package com.kaldar.kaldar.dispatch.application.service.impl;

import com.kaldar.kaldar.dispatch.application.dto.LogisticsDeliveryRequest;
import com.kaldar.kaldar.dispatch.application.dto.LogisticsDeliveryResponse;
import com.kaldar.kaldar.dispatch.application.dto.ChowdeckRelayRequest;
import com.kaldar.kaldar.dispatch.application.dto.ChowdeckRelayResponse;
import com.kaldar.kaldar.dispatch.application.dto.LocationDetails;
import com.kaldar.kaldar.dispatch.application.dto.RelayData;
import com.kaldar.kaldar.dispatch.application.service.LogisticsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class ChowdeckLogisticsProvider implements LogisticsProvider {
    private static final Logger log = LoggerFactory.getLogger(ChowdeckLogisticsProvider.class);

    private final RestTemplate restTemplate;
    
    @Value("${logistics.chowdeck.api-key:}")
    private String apiKey;

    @Value("${logistics.chowdeck.base-url:https://api.chowdeck.com/relay}")
    private String baseUrl;

    public ChowdeckLogisticsProvider() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String getProviderName() {
        return "CHOWDECK";
    }

    @Override
    public LogisticsDeliveryResponse requestDelivery(LogisticsDeliveryRequest request) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("Chowdeck Relay API key is not configured. Falling back to simulated response for testing.");
            String mockExternalId = "chowdeck-mock-" + UUID.randomUUID().toString().substring(0, 8);
            return new LogisticsDeliveryResponse(mockExternalId, "preparing", "https://chowdeck.com/track/" + mockExternalId, "0.0");
        }

        try {
            // Chowdeck Relay API create delivery endpoint: POST /relay/delivery
            String url = baseUrl + "/delivery";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // Populate type-safe request payload
            LocationDetails pickup = new LocationDetails(
                    request.getPickupAddress(),
                    request.getPickupName(),
                    request.getPickupPhone(),
                    request.getPickupNotes() != null ? request.getPickupNotes() : "Pick up laundry clothes"
            );

            LocationDetails dropoff = new LocationDetails(
                    request.getDropoffAddress(),
                    request.getDropoffName(),
                    request.getDropoffPhone(),
                    request.getDropoffNotes() != null ? request.getDropoffNotes() : "Drop off laundry clothes"
            );

            String reference = "KLD-" + request.getOrderId() + "-" + System.currentTimeMillis();
            ChowdeckRelayRequest apiRequest = new ChowdeckRelayRequest(reference, pickup, dropoff);

            HttpEntity<ChowdeckRelayRequest> entity = new HttpEntity<>(apiRequest, headers);
            
            log.info("Sending delivery request to Chowdeck Relay API. Reference: {}", reference);
            ResponseEntity<ChowdeckRelayResponse> response = restTemplate.postForEntity(url, entity, ChowdeckRelayResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ChowdeckRelayResponse res = response.getBody();
                if ("success".equalsIgnoreCase(res.getStatus()) && res.getData() != null) {
                    RelayData data = res.getData();
                    
                    String externalId = data.getId() != null ? String.valueOf(data.getId()) : data.getReference();
                    String trackingUrl = data.getTrackingUrl() != null ? data.getTrackingUrl() : "";
                    String fee = data.getDeliveryPrice() != null ? String.valueOf(data.getDeliveryPrice()) : "0.0";
                    String status = data.getStatus() != null ? data.getStatus() : "preparing";
                    
                    return new LogisticsDeliveryResponse(externalId, status, trackingUrl, fee);
                } else {
                    log.error("Chowdeck Relay returned failure response message: {}", res.getMessage());
                    throw new RuntimeException("Chowdeck Relay error: " + res.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to request delivery from Chowdeck Relay API: {}", e.getMessage(), e);
            throw new RuntimeException("Chowdeck Relay delivery booking failed: " + e.getMessage(), e);
        }

        throw new RuntimeException("Chowdeck Relay delivery booking failed: Empty response received");
    }

    @Override
    public void cancelDelivery(String externalDeliveryId) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("Chowdeck Relay API key is not configured. Mocking cancellation.");
            return;
        }

        try {
            // Chowdeck Relay API cancel delivery endpoint: POST /relay/delivery/cancel
            String url = baseUrl + "/delivery/cancel";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // Construct payload mapping for cancellation
            java.util.Map<String, String> body = java.util.Map.of("reference", externalDeliveryId);
            HttpEntity<java.util.Map<String, String>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<ChowdeckRelayResponse> response = restTemplate.postForEntity(url, entity, ChowdeckRelayResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ChowdeckRelayResponse res = response.getBody();
                if ("success".equalsIgnoreCase(res.getStatus())) {
                    log.info("Successfully cancelled Chowdeck Relay delivery: {}", res.getMessage());
                } else {
                    log.error("Failed to cancel Chowdeck Relay delivery: {}", res.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to cancel Chowdeck Relay delivery {}: {}", externalDeliveryId, e.getMessage());
        }
    }
}
