package com.kaldar.kaldar.wallet.api;

import com.kaldar.kaldar.wallet.application.dto.WalletSummaryResponse;
import com.kaldar.kaldar.wallet.application.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{userId}/summary")
    public ResponseEntity<WalletSummaryResponse> getWalletSummary(@PathVariable Long userId) {
        WalletSummaryResponse summary = walletService.getWalletSummary(userId);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/credit")
    public ResponseEntity<java.util.Map<String, String>> creditWallet(@RequestBody com.kaldar.kaldar.wallet.application.dto.WalletCreditRequest request) {
        walletService.creditWallet(request);
        return ResponseEntity.ok(java.util.Map.of("status", "success", "message", "Wallet credited successfully"));
    }

    @PostMapping("/debit")
    public ResponseEntity<java.util.Map<String, String>> debitWallet(@RequestBody com.kaldar.kaldar.wallet.application.dto.WalletDebitRequest request) {
        walletService.debitWallet(request);
        return ResponseEntity.ok(java.util.Map.of("status", "success", "message", "Wallet debited successfully"));
    }
}
