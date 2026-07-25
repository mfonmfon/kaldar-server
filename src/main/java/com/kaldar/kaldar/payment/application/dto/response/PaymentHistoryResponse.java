package com.kaldar.kaldar.payment.application.dto.response;

import com.kaldar.kaldar.payment.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentHistoryResponse {

    private Long id;
    private BigDecimal amount;
    private PaymentStatus status;
    private String channel;
    private String reference;
    private LocalDateTime createdAt;

    public PaymentHistoryResponse() {}

    public PaymentHistoryResponse(Long id, BigDecimal amount, PaymentStatus status,
                                   String channel, String reference, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.channel = channel;
        this.reference = reference;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
