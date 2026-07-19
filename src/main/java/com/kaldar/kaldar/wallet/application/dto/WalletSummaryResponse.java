package com.kaldar.kaldar.wallet.application.dto;

import java.math.BigDecimal;
import java.util.List;

public class WalletSummaryResponse {
    private BigDecimal balance;
    private List<WalletTransactionDto> transactionHistory;

    public WalletSummaryResponse() {}

    public WalletSummaryResponse(BigDecimal balance, List<WalletTransactionDto> transactionHistory) {
        this.balance = balance;
        this.transactionHistory = transactionHistory;
    }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public List<WalletTransactionDto> getTransactionHistory() { return transactionHistory; }
    public void setTransactionHistory(List<WalletTransactionDto> transactionHistory) { this.transactionHistory = transactionHistory; }
}
