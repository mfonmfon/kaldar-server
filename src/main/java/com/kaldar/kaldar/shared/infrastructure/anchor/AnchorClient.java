package com.kaldar.kaldar.shared.infrastructure.anchor;

import com.kaldar.kaldar.shared.domain.exceptions.PaymentProcessingException;
import com.kaldar.kaldar.shared.infrastructure.anchor.dto.AnchorBookTransferRequest;
import com.kaldar.kaldar.shared.infrastructure.anchor.dto.AnchorPayoutRequest;
import com.kaldar.kaldar.shared.infrastructure.anchor.dto.AnchorSubAccountRequest;
import com.kaldar.kaldar.shared.infrastructure.anchor.dto.AnchorSubAccountResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AnchorClient {

    private static final Logger log = LoggerFactory.getLogger(AnchorClient.class);

    private final String secretKey;
    private final String baseUrl;
    private final RestTemplate restTemplate;

    public AnchorClient(
            @Value("${anchor.secret-key:}") String secretKey,
            @Value("${anchor.base-url:https://api.getanchor.co}") String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public AnchorSubAccountResponse createSubAccount(AnchorSubAccountRequest request) {
        if (isInSandboxMode()) {
            return mockSubAccountResponse(request);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("fullName", request.getFullName());
        body.put("email", request.getEmail());
        body.put("phoneNumber", request.getPhoneNumber());

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/sub-accounts", HttpMethod.POST,
                    new HttpEntity<>(body, authHeaders()), Map.class);

            Map<String, Object> data = extractData(response, "createSubAccount");
            return new AnchorSubAccountResponse(
                    (String) data.get("id"),
                    (String) data.get("accountNumber"),
                    (String) data.get("bankName"),
                    (String) data.get("accountName"));

        } catch (RestClientException ex) {
            log.error("Anchor createSubAccount failed", ex);
            throw new PaymentProcessingException("Failed to provision virtual account. Please try again.");
        }
    }

    public void performBookTransfer(AnchorBookTransferRequest request) {
        if (isInSandboxMode()) {
            log.info("Anchor sandbox book transfer: src={} dst={} amount={} ref={}",
                    request.getSourceAccountId(), request.getDestinationAccountId(),
                    request.getAmount(), request.getReference());
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("sourceAccountId", request.getSourceAccountId());
        body.put("destinationAccountId", request.getDestinationAccountId());
        body.put("amount", request.getAmount());
        body.put("reference", request.getReference());
        body.put("description", request.getDescription());

        try {
            restTemplate.exchange(
                    baseUrl + "/transfers/book", HttpMethod.POST,
                    new HttpEntity<>(body, authHeaders()), Map.class);
        } catch (RestClientException ex) {
            log.error("Anchor book transfer failed ref={}", request.getReference(), ex);
            throw new PaymentProcessingException("Payment transfer failed. Please contact support.");
        }
    }

    public void performPayoutWithdrawal(AnchorPayoutRequest request) {
        if (isInSandboxMode()) {
            log.info("Anchor sandbox payout: src={} bank={} acct={} amount={} ref={}",
                    request.getSourceAccountId(), request.getDestinationBankCode(),
                    request.getDestinationAccountNumber(), request.getAmount(), request.getReference());
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("sourceAccountId", request.getSourceAccountId());
        body.put("destinationBankCode", request.getDestinationBankCode());
        body.put("destinationAccountNumber", request.getDestinationAccountNumber());
        body.put("destinationAccountName", request.getDestinationAccountName());
        body.put("amount", request.getAmount());
        body.put("reference", request.getReference());
        body.put("narration", request.getNarration());

        try {
            restTemplate.exchange(
                    baseUrl + "/transfers/out", HttpMethod.POST,
                    new HttpEntity<>(body, authHeaders()), Map.class);
        } catch (RestClientException ex) {
            log.error("Anchor payout failed ref={}", request.getReference(), ex);
            throw new PaymentProcessingException("Payout withdrawal failed. Please try again.");
        }
    }

    private boolean isInSandboxMode() {
        return secretKey == null || secretKey.isBlank();
    }

    private AnchorSubAccountResponse mockSubAccountResponse(AnchorSubAccountRequest request) {
        String mockId = "anc_acc_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String mockAccountNumber = "99" + String.format("%08d", Math.abs(request.getEmail().hashCode() % 100_000_000));
        log.info("Anchor sandbox createSubAccount: email={} mockAccount={}", request.getEmail(), mockAccountNumber);
        return new AnchorSubAccountResponse(mockId, mockAccountNumber, "Anchor Microfinance Bank", request.getFullName());
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(secretKey);
        return headers;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractData(ResponseEntity<Map> response, String operation) {
        if (response.getBody() == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new PaymentProcessingException("Anchor " + operation + " returned an unexpected response");
        }
        return (Map<String, Object>) response.getBody().get("data");
    }
}
