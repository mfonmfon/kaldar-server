package com.kaldar.kaldar.drycleaner.application.dto.request;

import com.kaldar.kaldar.shared.domain.constants.BusinessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VerifyBusinessRequest {

    @NotNull(message = "Dry cleaner ID is required")
    private Long dryCleanerId;

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotNull(message = "Business type is required")
    private BusinessType businessType;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotBlank(message = "Tax identification number is required")
    private String taxIdentificationNumber;

    // Note: cacDocumentUrl is no longer part of the request body.
    // The CAC document is sent as a separate multipart file part ("cacDocument")
    // and the controller resolves the Cloudinary URL before calling the service.

    public Long getDryCleanerId() {
        return dryCleanerId;
    }

    public void setDryCleanerId(Long dryCleanerId) {
        this.dryCleanerId = dryCleanerId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getTaxIdentificationNumber() {
        return taxIdentificationNumber;
    }

    public void setTaxIdentificationNumber(String taxIdentificationNumber) {
        this.taxIdentificationNumber = taxIdentificationNumber;
    }
}
