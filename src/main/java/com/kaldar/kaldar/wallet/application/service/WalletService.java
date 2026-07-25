package com.kaldar.kaldar.wallet.application.service;

import com.kaldar.kaldar.wallet.application.dto.WalletBalanceResponse;
import com.kaldar.kaldar.wallet.application.dto.WalletCreditRequest;
import com.kaldar.kaldar.wallet.application.dto.WalletDebitRequest;
import com.kaldar.kaldar.wallet.application.dto.WalletSummaryResponse;

public interface WalletService {

    void creditWallet(WalletCreditRequest request);

    void debitWallet(WalletDebitRequest request);

    WalletBalanceResponse getWalletBalance(Long userId);

    WalletSummaryResponse getWalletSummary(Long userId);

    void createVirtualAccountForUser(Long userId);
}
