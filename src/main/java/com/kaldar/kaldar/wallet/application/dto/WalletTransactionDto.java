package com.kaldar.kaldar.wallet.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletTransactionDto {
    private Long id;
    private BigDecimal amount;
    private String type;
    private String description;
    private String reference;
    private LocalDateTime createdAt;

    public WalletTransactionDto() {}

    public WalletTransactionDto(Long id, BigDecimal amount, String type, String description, String reference, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.reference = reference;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
