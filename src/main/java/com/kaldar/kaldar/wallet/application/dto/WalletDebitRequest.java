package com.kaldar.kaldar.wallet.application.dto;

import java.math.BigDecimal;

public class WalletDebitRequest {
    private Long userId;
    private BigDecimal amount;
    private String description;
    private String reference;

    public WalletDebitRequest() {}

    public WalletDebitRequest(Long userId, BigDecimal amount, String description, String reference) {
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.reference = reference;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}
