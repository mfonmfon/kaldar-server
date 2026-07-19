package com.kaldar.kaldar.drycleaner.api;

import com.kaldar.kaldar.shared.api.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PayoutController {

    @GetMapping("/verify-account")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyAccount(
            @RequestParam String account_number,
            @RequestParam String bank_code) {
        
        // Mocking account verification
        // In a real app, you would call Paystack or Flutterwave here
        
        Map<String, Object> data = new HashMap<>();
        data.put("account_name", "KALDAR TEST USER");
        data.put("account_number", account_number);
        data.put("bank_code", bank_code);

        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.<Map<String, Object>>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Account verified")
                .data(data)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
