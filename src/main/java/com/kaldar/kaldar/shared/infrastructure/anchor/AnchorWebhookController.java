package com.kaldar.kaldar.shared.infrastructure.anchor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaldar.kaldar.shared.api.response.ApiResponse;
import com.kaldar.kaldar.wallet.application.dto.WalletCreditRequest;
import com.kaldar.kaldar.wallet.application.service.WalletService;
import com.kaldar.kaldar.wallet.domain.model.Wallet;
import com.kaldar.kaldar.wallet.domain.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/anchor")
public class AnchorWebhookController {

    private static final Logger log = LoggerFactory.getLogger(AnchorWebhookController.class);

    private static final String EVENT_DEPOSIT_SUCCESSFUL = "deposit.successful";
    private static final String EVENT_CREDIT_SUCCESSFUL = "credit.successful";
    private static final String HMAC_ALGORITHM = "HmacSHA512";

    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final ObjectMapper objectMapper;
    private final String webhookSecret;

    public AnchorWebhookController(WalletService walletService,
                                   WalletRepository walletRepository,
                                   ObjectMapper objectMapper,
                                   @Value("${anchor.webhook-secret:}") String webhookSecret) {
        this.walletService = walletService;
        this.walletRepository = walletRepository;
        this.objectMapper = objectMapper;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<Void>> handleWebhook(
            @RequestHeader(value = "x-anchor-signature", required = false) String signature,
            @RequestBody String rawPayload) {

        if (signatureVerificationEnabled() && !isValidSignature(rawPayload, signature)) {
            log.warn("Anchor webhook rejected: invalid HMAC signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponse.<Void>builder()
                            .isSuccess(false)
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Invalid webhook signature")
                            .build());
        }

        try {
            JsonNode root = objectMapper.readTree(rawPayload);
            String event = root.path("event").asText();

            if (EVENT_DEPOSIT_SUCCESSFUL.equalsIgnoreCase(event)
                    || EVENT_CREDIT_SUCCESSFUL.equalsIgnoreCase(event)) {
                processDeposit(root.path("data"));
            } else {
                log.info("Anchor webhook: ignoring event type '{}'", event);
            }
        } catch (Exception ex) {
            log.error("Anchor webhook: failed to process payload", ex);
        }

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Webhook received")
                .build());
    }

    private void processDeposit(JsonNode data) {
        String accountNumber = data.path("accountNumber").asText(null);
        String anchorAccountId = data.path("accountId").asText(null);
        BigDecimal amount = new BigDecimal(data.path("amount").asText("0"));
        String reference = data.path("reference").asText("ANC-" + System.currentTimeMillis());

        Optional<Wallet> wallet = resolveWallet(accountNumber, anchorAccountId);

        if (wallet.isEmpty()) {
            log.warn("Anchor webhook: no wallet found for accountNumber={} anchorAccountId={}", accountNumber, anchorAccountId);
            return;
        }

        Long userId = wallet.get().getUser().getId();
        walletService.creditWallet(new WalletCreditRequest(userId, amount, "Bank deposit via Anchor", reference));
        log.info("Anchor webhook: credited {} NGN to wallet of user {}", amount, userId);
    }

    private Optional<Wallet> resolveWallet(String accountNumber, String anchorAccountId) {
        if (accountNumber != null && !accountNumber.isBlank()) {
            Optional<Wallet> wallet = walletRepository.findByVirtualAccountNumber(accountNumber);
            if (wallet.isPresent()) return wallet;
        }
        if (anchorAccountId != null && !anchorAccountId.isBlank()) {
            return walletRepository.findByAnchorAccountId(anchorAccountId);
        }
        return Optional.empty();
    }

    private boolean signatureVerificationEnabled() {
        return webhookSecret != null && !webhookSecret.isBlank();
    }

    private boolean isValidSignature(String payload, String signature) {
        if (signature == null || signature.isBlank()) return false;
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            String computed = HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
            return computed.equalsIgnoreCase(signature);
        } catch (Exception ex) {
            log.error("Anchor webhook: HMAC computation failed", ex);
            return false;
        }
    }
}
