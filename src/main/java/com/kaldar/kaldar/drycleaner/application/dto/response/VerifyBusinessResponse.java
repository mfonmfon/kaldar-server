package com.kaldar.kaldar.drycleaner.application.dto.response;

import com.kaldar.kaldar.shared.domain.constants.BusinessType;
import com.kaldar.kaldar.shared.domain.constants.VerificationStatus;

import java.time.LocalDateTime;

public class VerifyBusinessResponse {

    private Long verificationId;
    private Long dryCleanerId;
    private String businessName;
    private BusinessType businessType;
    private String registrationNumber;
    private VerificationStatus verificationStatus;
    private LocalDateTime submittedAt;
    private String message;

    public Long getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(Long verificationId) {
        this.verificationId = verificationId;
    }

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

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
