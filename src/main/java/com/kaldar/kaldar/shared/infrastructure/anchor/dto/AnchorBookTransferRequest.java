package com.kaldar.kaldar.shared.infrastructure.anchor.dto;

import java.math.BigDecimal;

public class AnchorBookTransferRequest {

    private final String sourceAccountId;
    private final String destinationAccountId;
    private final BigDecimal amount;
    private final String reference;
    private final String description;

    public AnchorBookTransferRequest(String sourceAccountId, String destinationAccountId,
                                     BigDecimal amount, String reference, String description) {
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.reference = reference;
        this.description = description;
    }

    public String getSourceAccountId() { return sourceAccountId; }
    public String getDestinationAccountId() { return destinationAccountId; }
    public BigDecimal getAmount() { return amount; }
    public String getReference() { return reference; }
    public String getDescription() { return description; }
}
