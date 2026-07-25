package com.kaldar.kaldar.drycleaner.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Request body for dry cleaner earnings payout withdrawal.
 */
public class PayoutWithdrawalRequest {

    @NotNull(message = "dryCleanerId is required")
    private Long dryCleanerId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "1.00", message = "Withdrawal amount must be at least ₦1.00")
    private BigDecimal amount;

    public PayoutWithdrawalRequest() {}

    public PayoutWithdrawalRequest(Long dryCleanerId, BigDecimal amount) {
        this.dryCleanerId = dryCleanerId;
        this.amount       = amount;
    }

    public Long getDryCleanerId()                  { return dryCleanerId; }
    public void setDryCleanerId(Long dryCleanerId) { this.dryCleanerId = dryCleanerId; }

    public BigDecimal getAmount()             { return amount; }
    public void setAmount(BigDecimal amount)  { this.amount = amount; }
}
