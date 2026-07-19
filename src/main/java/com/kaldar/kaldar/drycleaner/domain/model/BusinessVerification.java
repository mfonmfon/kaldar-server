package com.kaldar.kaldar.drycleaner.domain.model;

import com.kaldar.kaldar.shared.domain.constants.BusinessType;
import com.kaldar.kaldar.shared.domain.constants.VerificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "business_verification")
public class BusinessVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "dry_cleaner_id", nullable = false, unique = true)
    private DryCleanerEntity dryCleaner;

    @NotBlank(message = "Business name is required")
    @Column(nullable = false)
    private String businessName;

    @NotNull(message = "Business type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessType businessType;

    @NotBlank(message = "Registration number is required")
    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @NotBlank(message = "Tax identification number is required")
    @Column(nullable = false, unique = true)
    private String taxIdentificationNumber;

    @NotBlank(message = "CAC document URL is required")
    @Column(nullable = false)
    private String cacDocumentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus;

    private String rejectionReason;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    private LocalDateTime verifiedAt;

    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DryCleanerEntity getDryCleaner() {
        return dryCleaner;
    }

    public void setDryCleaner(DryCleanerEntity dryCleaner) {
        this.dryCleaner = dryCleaner;
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

    public String getCacDocumentUrl() {
        return cacDocumentUrl;
    }

    public void setCacDocumentUrl(String cacDocumentUrl) {
        this.cacDocumentUrl = cacDocumentUrl;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
