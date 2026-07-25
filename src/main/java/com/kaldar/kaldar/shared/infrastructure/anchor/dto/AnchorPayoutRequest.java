package com.kaldar.kaldar.shared.infrastructure.anchor.dto;

import java.math.BigDecimal;

public class AnchorPayoutRequest {

    private final String sourceAccountId;
    private final String destinationBankCode;
    private final String destinationAccountNumber;
    private final String destinationAccountName;
    private final BigDecimal amount;
    private final String reference;
    private final String narration;

    public AnchorPayoutRequest(String sourceAccountId, String destinationBankCode,
                               String destinationAccountNumber, String destinationAccountName,
                               BigDecimal amount, String reference, String narration) {
        this.sourceAccountId = sourceAccountId;
        this.destinationBankCode = destinationBankCode;
        this.destinationAccountNumber = destinationAccountNumber;
        this.destinationAccountName = destinationAccountName;
        this.amount = amount;
        this.reference = reference;
        this.narration = narration;
    }

    public String getSourceAccountId() { return sourceAccountId; }
    public String getDestinationBankCode() { return destinationBankCode; }
    public String getDestinationAccountNumber() { return destinationAccountNumber; }
    public String getDestinationAccountName() { return destinationAccountName; }
    public BigDecimal getAmount() { return amount; }
    public String getReference() { return reference; }
    public String getNarration() { return narration; }
}
