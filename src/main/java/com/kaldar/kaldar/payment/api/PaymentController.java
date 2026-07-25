package com.kaldar.kaldar.payment.api;

import com.kaldar.kaldar.payment.application.dto.request.InitiatePaymentRequest;
import com.kaldar.kaldar.payment.application.dto.response.PaymentHistoryResponse;
import com.kaldar.kaldar.payment.application.dto.response.PaymentInitResponse;
import com.kaldar.kaldar.payment.application.service.PaymentService;
import com.kaldar.kaldar.shared.api.response.ApiResponse;
import com.kaldar.kaldar.shared.infrastructure.utility.CurrentUserResolver;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.*;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final CurrentUserResolver currentUserResolver;

    public PaymentController(PaymentService paymentService,
                              CurrentUserResolver currentUserResolver) {
        this.paymentService = paymentService;
        this.currentUserResolver = currentUserResolver;
    }

    /**
     * Initiates a Paystack wallet top-up.
     * Returns the Paystack authorization URL and reference for the client to redirect to.
     */
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentInitResponse>> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request) {

        Long userId = currentUserResolver.getCurrentUserId();
        PaymentInitResponse initResponse = paymentService.initiatePayment(userId, request.getAmount());
        ApiResponse<PaymentInitResponse> response = ApiResponse.<PaymentInitResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(PAYMENT_INITIATED.getMessage())
                .data(initResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Paystack webhook callback.
     * This endpoint is public — authentication is performed via HMAC-SHA512 signature
     * verification inside the service layer, not Spring Security.
     */
    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<Void>> handleWebhook(
            @RequestHeader("x-paystack-signature") String signature,
            @RequestBody String rawPayload) {

        paymentService.handleWebhook(signature, rawPayload);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(PAYMENT_WEBHOOK_RECEIVED.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Returns the authenticated customer's payment transaction history.
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<PaymentHistoryResponse>>> getPaymentHistory() {
        Long userId = currentUserResolver.getCurrentUserId();
        List<PaymentHistoryResponse> history = paymentService.getPaymentHistory(userId);
        ApiResponse<List<PaymentHistoryResponse>> response = ApiResponse.<List<PaymentHistoryResponse>>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(PAYMENT_HISTORY_FETCHED.getMessage())
                .data(history)
                .build();
        return ResponseEntity.ok(response);
    }
}
