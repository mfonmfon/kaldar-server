package com.kaldar.kaldar.payment.application.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaldar.kaldar.payment.application.dto.request.PaystackWebhookPayload;
import com.kaldar.kaldar.payment.application.dto.response.PaymentHistoryResponse;
import com.kaldar.kaldar.payment.application.dto.response.PaymentInitResponse;
import com.kaldar.kaldar.payment.application.service.PaymentService;
import com.kaldar.kaldar.payment.domain.model.PaymentStatus;
import com.kaldar.kaldar.payment.domain.model.PaymentTransaction;
import com.kaldar.kaldar.payment.domain.repository.PaymentTransactionRepository;
import com.kaldar.kaldar.payment.infrastructure.payment.PaystackClient;
import com.kaldar.kaldar.payment.infrastructure.payment.PaystackInitData;
import com.kaldar.kaldar.shared.domain.exceptions.UserNotFoundException;
import com.kaldar.kaldar.shared.domain.model.UserEntity;
import com.kaldar.kaldar.shared.domain.repository.UserEntityRepository;
import com.kaldar.kaldar.wallet.application.dto.WalletCreditRequest;
import com.kaldar.kaldar.wallet.application.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
public class DefaultPaymentService implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(DefaultPaymentService.class);

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaystackClient paystackClient;
    private final WalletService walletService;
    private final UserEntityRepository userEntityRepository;
    private final ObjectMapper objectMapper;
    private final String paystackSecretKey;

    public DefaultPaymentService(
            PaymentTransactionRepository paymentTransactionRepository,
            PaystackClient paystackClient,
            WalletService walletService,
            UserEntityRepository userEntityRepository,
            ObjectMapper objectMapper,
            @Value("${paystack.secret-key:}") String paystackSecretKey) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.paystackClient = paystackClient;
        this.walletService = walletService;
        this.userEntityRepository = userEntityRepository;
        this.objectMapper = objectMapper;
        this.paystackSecretKey = paystackSecretKey;
    }

    @Override
    @Transactional
    public PaymentInitResponse initiatePayment(Long userId, BigDecimal amount) {
        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        String reference = "KLD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

        PaystackInitData initData =
                paystackClient.initializeTransaction(user.getEmail(), amount, reference);

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setReference(reference);
        transaction.setStatus(PaymentStatus.PENDING);
        paymentTransactionRepository.save(transaction);

        return new PaymentInitResponse(
                initData.getAuthorizationUrl(),
                initData.getReference(),
                initData.getAccessCode()
        );
    }

    @Override
    @Transactional
    public void handleWebhook(String paystackSignature, String rawPayload) {
        // 1. Verify HMAC-SHA512 signature
        if (!isSignatureValid(paystackSignature, rawPayload)) {
            log.warn("Paystack webhook: invalid signature — ignoring payload");
            return;
        }

        // 2. Parse the payload
        PaystackWebhookPayload payload;
        try {
            payload = objectMapper.readValue(rawPayload, PaystackWebhookPayload.class);
        } catch (Exception e) {
            log.error("Paystack webhook: failed to parse payload", e);
            return;
        }

        // 3. Only handle charge.success events
        if (!"charge.success".equals(payload.getEvent())) {
            log.info("Paystack webhook: ignoring event '{}'", payload.getEvent());
            return;
        }

        PaystackWebhookPayload.Data data = payload.getData();
        if (data == null || data.getReference() == null) {
            log.warn("Paystack webhook: missing reference in payload");
            return;
        }

        // 4. Look up our transaction record
        PaymentTransaction transaction = paymentTransactionRepository
                .findByReference(data.getReference())
                .orElse(null);

        if (transaction == null) {
            log.warn("Paystack webhook: no transaction found for reference '{}'", data.getReference());
            return;
        }

        // 5. Idempotency: skip if already processed
        if (transaction.getStatus() != PaymentStatus.PENDING) {
            log.info("Paystack webhook: transaction '{}' already in status '{}' — skipping",
                    data.getReference(), transaction.getStatus());
            return;
        }

        // 6. Resolve final status
        boolean isSuccess = "success".equalsIgnoreCase(data.getStatus());
        transaction.setStatus(isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        transaction.setChannel(data.getChannel());
        transaction.setGatewayResponse(data.getGatewayResponse());
        transaction.setUpdatedAt(LocalDateTime.now());
        paymentTransactionRepository.save(transaction);

        // 7. Credit wallet on success
        if (isSuccess) {
            WalletCreditRequest creditRequest = new WalletCreditRequest();
            creditRequest.setUserId(transaction.getUserId());
            creditRequest.setAmount(transaction.getAmount());
            creditRequest.setReference(transaction.getReference());
            creditRequest.setDescription("Wallet top-up via Paystack");
            walletService.creditWallet(creditRequest);
            log.info("Wallet credited ₦{} for user {} via reference {}",
                    transaction.getAmount(), transaction.getUserId(), transaction.getReference());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentHistoryResponse> getPaymentHistory(Long userId) {
        return paymentTransactionRepository.findByUserId(userId).stream()
                .map(tx -> new PaymentHistoryResponse(
                        tx.getId(),
                        tx.getAmount(),
                        tx.getStatus(),
                        tx.getChannel(),
                        tx.getReference(),
                        tx.getCreatedAt()
                ))
                .toList();
    }

    // ─── HMAC-SHA512 signature verification ───────────────────────────────────

    private boolean isSignatureValid(String signature, String payload) {
        if (paystackSecretKey == null || paystackSecretKey.isBlank()) {
            log.warn("Paystack secret key not configured — skipping signature verification");
            return true; // permissive for dev; should be false in prod
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(
                    paystackSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computed = HexFormat.of().formatHex(hash);
            return computed.equalsIgnoreCase(signature);
        } catch (Exception e) {
            log.error("HMAC-SHA512 verification failed", e);
            return false;
        }
    }
}
