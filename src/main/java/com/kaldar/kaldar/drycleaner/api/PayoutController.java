package com.kaldar.kaldar.drycleaner.api;

import com.kaldar.kaldar.drycleaner.application.dto.request.PayoutWithdrawalRequest;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import com.kaldar.kaldar.drycleaner.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.shared.api.response.ApiResponse;
import com.kaldar.kaldar.shared.domain.exceptions.PayoutAccountNotConfiguredException;
import com.kaldar.kaldar.shared.domain.exceptions.UserNotFoundException;
import com.kaldar.kaldar.shared.infrastructure.anchor.AnchorClient;
import com.kaldar.kaldar.shared.infrastructure.anchor.dto.AnchorPayoutRequest;
import com.kaldar.kaldar.wallet.application.dto.WalletDebitRequest;
import com.kaldar.kaldar.wallet.application.service.WalletService;
import com.kaldar.kaldar.wallet.domain.model.Wallet;
import com.kaldar.kaldar.wallet.domain.repository.WalletRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drycleaner/payout")
public class PayoutController {

    private static final Logger log = LoggerFactory.getLogger(PayoutController.class);

    private final AnchorClient anchorClient;
    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final DryCleanerEntityRepository dryCleanerRepository;

    public PayoutController(AnchorClient anchorClient,
                            WalletService walletService,
                            WalletRepository walletRepository,
                            DryCleanerEntityRepository dryCleanerRepository) {
        this.anchorClient = anchorClient;
        this.walletService = walletService;
        this.walletRepository = walletRepository;
        this.dryCleanerRepository = dryCleanerRepository;
    }

    @GetMapping("/verify-account")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyAccount(
            @RequestParam String account_number,
            @RequestParam String bank_code) {

        Map<String, Object> data = new HashMap<>();
        data.put("account_number", account_number);
        data.put("bank_code", bank_code);

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Account verified")
                .data(data)
                .build());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdrawEarnings(
            @Valid @RequestBody PayoutWithdrawalRequest request) {

        DryCleanerEntity dryCleaner = dryCleanerRepository.findById(request.getDryCleanerId())
                .orElseThrow(() -> new UserNotFoundException("Dry cleaner not found"));

        validatePayoutAccountConfigured(dryCleaner);

        Wallet wallet = walletRepository.findByUserId(dryCleaner.getId())
                .orElseThrow(() -> new UserNotFoundException("Wallet not found for dry cleaner"));

        String reference = generateReference();
        String payoutAccountName = dryCleaner.getAccountName() != null
                ? dryCleaner.getAccountName()
                : dryCleaner.getBusinessName();

        walletService.debitWallet(new WalletDebitRequest(
                dryCleaner.getId(),
                request.getAmount(),
                "Earnings withdrawal to " + dryCleaner.getBankName(),
                reference));

        anchorClient.performPayoutWithdrawal(new AnchorPayoutRequest(
                wallet.getAnchorAccountId(),
                dryCleaner.getBankCode(),
                dryCleaner.getAccountNumber(),
                payoutAccountName,
                request.getAmount(),
                reference,
                "Kaldar earnings withdrawal"));

        log.info("Payout initiated: dryCleanerId={} amount={} ref={}", dryCleaner.getId(), request.getAmount(), reference);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Withdrawal initiated. Funds will arrive in your bank account shortly.")
                .build());
    }

    private void validatePayoutAccountConfigured(DryCleanerEntity dryCleaner) {
        if (dryCleaner.getAccountNumber() == null || dryCleaner.getBankCode() == null) {
            throw new PayoutAccountNotConfiguredException(
                    "No payout bank account configured. Please add your bank account details first.");
        }
    }

    private String generateReference() {
        return "PAYOUT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}
