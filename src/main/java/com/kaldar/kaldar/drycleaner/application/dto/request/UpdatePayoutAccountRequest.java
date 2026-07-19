package com.kaldar.kaldar.drycleaner.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdatePayoutAccountRequest {
    @NotNull(message = "Dry cleaner ID is required")
    private Long dryCleanerId;

    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Bank code is required")
    private String bankCode;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    // Getters and Setters
    public Long getDryCleanerId() { return dryCleanerId; }
    public void setDryCleanerId(Long dryCleanerId) { this.dryCleanerId = dryCleanerId; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
}
