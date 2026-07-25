package com.kaldar.kaldar.wallet.api;

import com.kaldar.kaldar.shared.api.response.ApiResponse;
import com.kaldar.kaldar.shared.infrastructure.utility.CurrentUserResolver;
import com.kaldar.kaldar.wallet.application.dto.WalletBalanceResponse;
import com.kaldar.kaldar.wallet.application.dto.WalletCreditRequest;
import com.kaldar.kaldar.wallet.application.dto.WalletDebitRequest;
import com.kaldar.kaldar.wallet.application.dto.WalletSummaryResponse;
import com.kaldar.kaldar.wallet.application.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.*;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;
    private final CurrentUserResolver currentUserResolver;

    public WalletController(WalletService walletService,
                            CurrentUserResolver currentUserResolver) {
        this.walletService = walletService;
        this.currentUserResolver = currentUserResolver;
    }

    /**
     * GET /api/v1/wallet/balance
     * Returns the authenticated customer's wallet balance in NGN.
     */
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<WalletBalanceResponse>> getBalance() {
        Long userId = currentUserResolver.getCurrentUserId();
        WalletBalanceResponse balance = walletService.getWalletBalance(userId);
        ApiResponse<WalletBalanceResponse> response = ApiResponse.<WalletBalanceResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(WALLET_BALANCE_FETCHED.getMessage())
                .data(balance)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/wallet/summary
     * Returns balance + full transaction history for a user (internal / admin use).
     */
    @GetMapping("/{userId}/summary")
    public ResponseEntity<ApiResponse<WalletSummaryResponse>> getWalletSummary(@PathVariable Long userId) {
        WalletSummaryResponse summary = walletService.getWalletSummary(userId);
        ApiResponse<WalletSummaryResponse> response = ApiResponse.<WalletSummaryResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(WALLET_BALANCE_FETCHED.getMessage())
                .data(summary)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/wallet/credit
     * Credits a wallet (internal — called by payment module on successful Paystack webhook).
     */
    @PostMapping("/credit")
    public ResponseEntity<ApiResponse<Void>> creditWallet(@Valid @RequestBody WalletCreditRequest request) {
        walletService.creditWallet(request);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Wallet credited successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/wallet/debit
     * Debits a wallet (internal — called by order module on payment).
     */
    @PostMapping("/debit")
    public ResponseEntity<ApiResponse<Void>> debitWallet(@Valid @RequestBody WalletDebitRequest request) {
        walletService.debitWallet(request);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Wallet debited successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
