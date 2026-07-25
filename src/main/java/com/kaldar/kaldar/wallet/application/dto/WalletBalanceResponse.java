package com.kaldar.kaldar.wallet.application.dto;

import java.math.BigDecimal;

public class WalletBalanceResponse {

    private BigDecimal balance;
    private String currency;

    public WalletBalanceResponse() {}

    public WalletBalanceResponse(BigDecimal balance, String currency) {
        this.balance = balance;
        this.currency = currency;
    }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
