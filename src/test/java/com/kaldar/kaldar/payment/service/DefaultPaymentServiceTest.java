package com.kaldar.kaldar.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.payment.application.dto.response.PaymentHistoryResponse;
import com.kaldar.kaldar.payment.application.dto.response.PaymentInitResponse;
import com.kaldar.kaldar.payment.application.service.impl.DefaultPaymentService;
import com.kaldar.kaldar.payment.domain.model.PaymentStatus;
import com.kaldar.kaldar.payment.domain.model.PaymentTransaction;
import com.kaldar.kaldar.payment.domain.repository.PaymentTransactionRepository;
import com.kaldar.kaldar.payment.infrastructure.payment.PaystackClient;
import com.kaldar.kaldar.payment.infrastructure.payment.PaystackInitData;
import com.kaldar.kaldar.shared.domain.exceptions.UserNotFoundException;
import com.kaldar.kaldar.shared.domain.repository.UserEntityRepository;
import com.kaldar.kaldar.wallet.application.dto.WalletCreditRequest;
import com.kaldar.kaldar.wallet.application.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultPaymentService Unit Tests")
class DefaultPaymentServiceTest {

    @Mock private PaymentTransactionRepository paymentTransactionRepository;
    @Mock private PaystackClient paystackClient;
    @Mock private WalletService walletService;
    @Mock private UserEntityRepository userEntityRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String secretKey = "test_secret_key";
    private DefaultPaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new DefaultPaymentService(
                paymentTransactionRepository,
                paystackClient,
                walletService,
                userEntityRepository,
                objectMapper,
                secretKey
        );
    }

    private CustomerEntity buildUser(Long id, String email) {
        CustomerEntity user = new CustomerEntity();
        user.setId(id);
        user.setEmail(email);
        return user;
    }

    private String calculateHmacSignature(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        mac.init(keySpec);
        byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }

    // =========================================================================
    // initiatePayment
    // =========================================================================

    @Nested
    @DisplayName("initiatePayment()")
    class InitiatePayment {

        @Test
        @DisplayName("should initiate payment successfully and create PENDING transaction")
        void shouldInitiatePaymentSuccessfully() {
            CustomerEntity user = buildUser(1L, "customer@kaldar.com");
            PaystackInitData initData = new PaystackInitData();
            initData.setAuthorizationUrl("https://checkout.paystack.com/auth123");
            initData.setAccessCode("access123");
            initData.setReference("KLD-REF123");

            when(userEntityRepository.findById(1L)).thenReturn(Optional.of(user));
            when(paystackClient.initializeTransaction(eq("customer@kaldar.com"), eq(new BigDecimal("5000.00")), anyString()))
                    .thenReturn(initData);
            when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenAnswer(inv -> inv.getArgument(0));

            PaymentInitResponse response = paymentService.initiatePayment(1L, new BigDecimal("5000.00"));

            assertThat(response).isNotNull();
            assertThat(response.getAuthorizationUrl()).isEqualTo("https://checkout.paystack.com/auth123");
            assertThat(response.getAccessCode()).isEqualTo("access123");

            ArgumentCaptor<PaymentTransaction> captor = ArgumentCaptor.forClass(PaymentTransaction.class);
            verify(paymentTransactionRepository).save(captor.capture());
            assertThat(captor.getValue().getUserId()).isEqualTo(1L);
            assertThat(captor.getValue().getAmount()).isEqualByComparingTo(new BigDecimal("5000.00"));
            assertThat(captor.getValue().getStatus()).isEqualTo(PaymentStatus.PENDING);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user does not exist")
        void shouldThrowWhenUserNotFound() {
            when(userEntityRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.initiatePayment(99L, new BigDecimal("1000.00")))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("99");

            verifyNoInteractions(paystackClient, paymentTransactionRepository);
        }
    }

    // =========================================================================
    // handleWebhook
    // =========================================================================

    @Nested
    @DisplayName("handleWebhook()")
    class HandleWebhook {

        @Test
        @DisplayName("should verify signature, update transaction status to SUCCESS, and credit wallet")
        void shouldProcessSuccessfulWebhookAndCreditWallet() throws Exception {
            String rawPayload = """
                    {
                        "event": "charge.success",
                        "data": {
                            "reference": "KLD-12345678",
                            "status": "success",
                            "amount": 500000,
                            "channel": "card",
                            "gateway_response": "Successful"
                        }
                    }
                    """;
            String signature = calculateHmacSignature(rawPayload, secretKey);

            PaymentTransaction tx = new PaymentTransaction();
            tx.setId(10L);
            tx.setUserId(1L);
            tx.setAmount(new BigDecimal("5000.00"));
            tx.setReference("KLD-12345678");
            tx.setStatus(PaymentStatus.PENDING);

            when(paymentTransactionRepository.findByReference("KLD-12345678")).thenReturn(Optional.of(tx));

            paymentService.handleWebhook(signature, rawPayload);

            verify(paymentTransactionRepository).save(tx);
            assertThat(tx.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
            assertThat(tx.getChannel()).isEqualTo("card");
            assertThat(tx.getGatewayResponse()).isEqualTo("Successful");

            ArgumentCaptor<WalletCreditRequest> creditCaptor = ArgumentCaptor.forClass(WalletCreditRequest.class);
            verify(walletService).creditWallet(creditCaptor.capture());
            assertThat(creditCaptor.getValue().getUserId()).isEqualTo(1L);
            assertThat(creditCaptor.getValue().getAmount()).isEqualByComparingTo(new BigDecimal("5000.00"));
            assertThat(creditCaptor.getValue().getReference()).isEqualTo("KLD-12345678");
        }

        @Test
        @DisplayName("should ignore webhook when signature is invalid")
        void shouldIgnoreWhenSignatureIsInvalid() {
            String rawPayload = "{\"event\":\"charge.success\"}";
            String invalidSignature = "invalid_signature_hash";

            paymentService.handleWebhook(invalidSignature, rawPayload);

            verifyNoInteractions(paymentTransactionRepository, walletService);
        }

        @Test
        @DisplayName("should ignore webhook when event is not charge.success")
        void shouldIgnoreNonChargeSuccessEvents() throws Exception {
            String rawPayload = """
                    {
                        "event": "transfer.success",
                        "data": { "reference": "KLD-123" }
                    }
                    """;
            String signature = calculateHmacSignature(rawPayload, secretKey);

            paymentService.handleWebhook(signature, rawPayload);

            verifyNoInteractions(paymentTransactionRepository, walletService);
        }

        @Test
        @DisplayName("should ignore webhook when transaction reference is not found in database")
        void shouldIgnoreWhenReferenceNotFound() throws Exception {
            String rawPayload = """
                    {
                        "event": "charge.success",
                        "data": { "reference": "KLD-UNKNOWN", "status": "success" }
                    }
                    """;
            String signature = calculateHmacSignature(rawPayload, secretKey);

            when(paymentTransactionRepository.findByReference("KLD-UNKNOWN")).thenReturn(Optional.empty());

            paymentService.handleWebhook(signature, rawPayload);

            verify(paymentTransactionRepository, never()).save(any());
            verifyNoInteractions(walletService);
        }

        @Test
        @DisplayName("should be idempotent and skip processing if transaction is already processed")
        void shouldBeIdempotentAndSkipAlreadyProcessedTransaction() throws Exception {
            String rawPayload = """
                    {
                        "event": "charge.success",
                        "data": { "reference": "KLD-123", "status": "success" }
                    }
                    """;
            String signature = calculateHmacSignature(rawPayload, secretKey);

            PaymentTransaction tx = new PaymentTransaction();
            tx.setReference("KLD-123");
            tx.setStatus(PaymentStatus.SUCCESS); // already processed

            when(paymentTransactionRepository.findByReference("KLD-123")).thenReturn(Optional.of(tx));

            paymentService.handleWebhook(signature, rawPayload);

            verify(paymentTransactionRepository, never()).save(any());
            verifyNoInteractions(walletService);
        }

        @Test
        @DisplayName("should set transaction status to FAILED and not credit wallet if paystack status is not success")
        void shouldHandleFailedWebhookWithoutCreditingWallet() throws Exception {
            String rawPayload = """
                    {
                        "event": "charge.success",
                        "data": {
                            "reference": "KLD-12345",
                            "status": "failed",
                            "channel": "bank",
                            "gateway_response": "Declined"
                        }
                    }
                    """;
            String signature = calculateHmacSignature(rawPayload, secretKey);

            PaymentTransaction tx = new PaymentTransaction();
            tx.setUserId(1L);
            tx.setAmount(new BigDecimal("2000.00"));
            tx.setReference("KLD-12345");
            tx.setStatus(PaymentStatus.PENDING);

            when(paymentTransactionRepository.findByReference("KLD-12345")).thenReturn(Optional.of(tx));

            paymentService.handleWebhook(signature, rawPayload);

            verify(paymentTransactionRepository).save(tx);
            assertThat(tx.getStatus()).isEqualTo(PaymentStatus.FAILED);
            verifyNoInteractions(walletService);
        }
    }

    // =========================================================================
    // getPaymentHistory
    // =========================================================================

    @Nested
    @DisplayName("getPaymentHistory()")
    class GetPaymentHistory {

        @Test
        @DisplayName("should return payment history for user")
        void shouldReturnPaymentHistory() {
            PaymentTransaction tx1 = new PaymentTransaction();
            tx1.setId(1L);
            tx1.setUserId(10L);
            tx1.setAmount(new BigDecimal("5000.00"));
            tx1.setStatus(PaymentStatus.SUCCESS);
            tx1.setChannel("card");
            tx1.setReference("KLD-001");
            tx1.setCreatedAt(LocalDateTime.now());

            when(paymentTransactionRepository.findByUserId(10L)).thenReturn(List.of(tx1));

            List<PaymentHistoryResponse> result = paymentService.getPaymentHistory(10L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getAmount()).isEqualByComparingTo(new BigDecimal("5000.00"));
            assertThat(result.get(0).getStatus()).isEqualTo(PaymentStatus.SUCCESS);
            assertThat(result.get(0).getReference()).isEqualTo("KLD-001");
        }
    }
}
