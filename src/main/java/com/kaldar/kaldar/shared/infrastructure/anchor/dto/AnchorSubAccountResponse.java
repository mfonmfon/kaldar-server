package com.kaldar.kaldar.shared.infrastructure.anchor.dto;

public class AnchorSubAccountResponse {

    private final String accountId;
    private final String accountNumber;
    private final String bankName;
    private final String accountName;

    public AnchorSubAccountResponse(String accountId, String accountNumber, String bankName, String accountName) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.accountName = accountName;
    }

    public String getAccountId() { return accountId; }
    public String getAccountNumber() { return accountNumber; }
    public String getBankName() { return bankName; }
    public String getAccountName() { return accountName; }
}
