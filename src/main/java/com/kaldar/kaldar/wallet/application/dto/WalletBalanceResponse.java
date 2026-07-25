package com.kaldar.kaldar.wallet.application.dto;

import java.math.BigDecimal;

/**
 * Response DTO for the wallet balance endpoint.
 * Includes the customer's dedicated Anchor virtual bank account details
 * so the frontend can display them on the "Fund Wallet" screen.
 */
public class WalletBalanceResponse {

    private final BigDecimal balance;
    private final String currency;
    private final String virtualAccountNumber;
    private final String virtualBankName;

    public WalletBalanceResponse(BigDecimal balance, String currency,
                                 String virtualAccountNumber, String virtualBankName) {
        this.balance               = balance;
        this.currency              = currency;
        this.virtualAccountNumber  = virtualAccountNumber;
        this.virtualBankName       = virtualBankName;
    }

    public BigDecimal getBalance()              { return balance; }
    public String getCurrency()                 { return currency; }
    public String getVirtualAccountNumber()     { return virtualAccountNumber; }
    public String getVirtualBankName()          { return virtualBankName; }
}
