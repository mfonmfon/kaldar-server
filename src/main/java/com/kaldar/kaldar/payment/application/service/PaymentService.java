package com.kaldar.kaldar.payment.application.service;

import com.kaldar.kaldar.payment.application.dto.response.PaymentHistoryResponse;
import com.kaldar.kaldar.payment.application.dto.response.PaymentInitResponse;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    /**
     * Initiates a Paystack wallet top-up for the given user.
     *
     * @param userId the authenticated customer's ID
     * @param amount the amount in NGN (Naira)
     * @return Paystack authorization URL + reference
     */
    PaymentInitResponse initiatePayment(Long userId, BigDecimal amount);

    /**
     * Processes a Paystack webhook event. Verifies the HMAC-SHA512 signature,
     * resolves the transaction, and credits the wallet on success.
     *
     * @param paystackSignature the {@code x-paystack-signature} header value
     * @param rawPayload        the raw request body string
     */
    void handleWebhook(String paystackSignature, String rawPayload);

    /**
     * Returns the payment history for the given user.
     */
    List<PaymentHistoryResponse> getPaymentHistory(Long userId);
}
