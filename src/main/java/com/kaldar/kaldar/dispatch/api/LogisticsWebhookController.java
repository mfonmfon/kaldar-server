package com.kaldar.kaldar.dispatch.api;

import com.kaldar.kaldar.dispatch.application.service.LogisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhooks/logistics")
public class LogisticsWebhookController {
    private static final Logger log = LoggerFactory.getLogger(LogisticsWebhookController.class);

    private final LogisticsService logisticsService;

    public LogisticsWebhookController(LogisticsService logisticsService) {
        this.logisticsService = logisticsService;
    }

    @PostMapping("/{provider}")
    public ResponseEntity<Map<String, Object>> handleWebhook(@PathVariable String provider,
                                                             @RequestBody Map<String, Object> payload,
                                                             @RequestHeader(value = "X-Chowdeck-Signature", required = false) String signature) {
        log.info("Received webhook request from provider: {}. Payload keys: {}", provider, payload.keySet());
        
        // signature validation logic can be added here once webhook secrets are configured.
        
        logisticsService.handleWebhookCallback(provider, payload);
        
        return ResponseEntity.ok(Map.of("status", "success", "message", "Webhook received successfully"));
    }
}
