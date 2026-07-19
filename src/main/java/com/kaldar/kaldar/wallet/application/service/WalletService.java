package com.kaldar.kaldar.wallet.application.service;

import com.kaldar.kaldar.wallet.application.dto.WalletSummaryResponse;
import java.math.BigDecimal;

public interface WalletService {
    void creditWallet(com.kaldar.kaldar.wallet.application.dto.WalletCreditRequest request);
    void debitWallet(com.kaldar.kaldar.wallet.application.dto.WalletDebitRequest request);
    BigDecimal getWalletBalance(Long userId);
    WalletSummaryResponse getWalletSummary(Long userId);
}
