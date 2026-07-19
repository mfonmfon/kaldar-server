package com.kaldar.kaldar.drycleaner.application.service.impl;

import com.kaldar.kaldar.drycleaner.application.dto.request.VerifyBusinessRequest;
import com.kaldar.kaldar.drycleaner.application.dto.response.VerifyBusinessResponse;
import com.kaldar.kaldar.drycleaner.application.service.BusinessVerificationService;
import com.kaldar.kaldar.drycleaner.domain.model.BusinessVerification;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import com.kaldar.kaldar.drycleaner.domain.repository.BusinessVerificationRepository;
import com.kaldar.kaldar.drycleaner.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.shared.domain.constants.VerificationStatus;
import com.kaldar.kaldar.shared.domain.exceptions.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.*;

@Service
public class DefaultBusinessVerificationService implements BusinessVerificationService {

    private final BusinessVerificationRepository businessVerificationRepository;
    private final DryCleanerEntityRepository dryCleanerEntityRepository;

    public DefaultBusinessVerificationService(
            BusinessVerificationRepository businessVerificationRepository,
            DryCleanerEntityRepository dryCleanerEntityRepository) {
        this.businessVerificationRepository = businessVerificationRepository;
        this.dryCleanerEntityRepository = dryCleanerEntityRepository;
    }

    @Override
    @Transactional
    public VerifyBusinessResponse submitBusinessVerification(VerifyBusinessRequest request, String cacDocumentUrl) {
        DryCleanerEntity dryCleaner = findDryCleanerById(request.getDryCleanerId());

        validateBusinessVerification(request);

        BusinessVerification verification = buildBusinessVerification(request, dryCleaner, cacDocumentUrl);
        BusinessVerification savedVerification = businessVerificationRepository.save(verification);

        return buildVerifyBusinessResponse(savedVerification);
    }

    @Override
    public VerifyBusinessResponse getVerificationStatus(Long dryCleanerId) {
        findDryCleanerById(dryCleanerId);

        BusinessVerification verification = businessVerificationRepository
                .findByDryCleanerId(dryCleanerId)
                .orElseThrow(() -> new UserNotFoundException("Business verification not found"));

        return buildVerifyBusinessResponse(verification);
    }

    private DryCleanerEntity findDryCleanerById(Long dryCleanerId) {
        return dryCleanerEntityRepository.findById(dryCleanerId)
                .orElseThrow(() -> new UserNotFoundException(
                        DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
    }

    private void validateBusinessVerification(VerifyBusinessRequest request) {
        if (businessVerificationRepository.findByDryCleanerId(request.getDryCleanerId()).isPresent()) {
            throw new BusinessAlreadyVerifiedException("Business verification already submitted");
        }

        if (businessVerificationRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateRegistrationNumberException("Registration number already exists");
        }

        if (businessVerificationRepository.existsByTaxIdentificationNumber(request.getTaxIdentificationNumber())) {
            throw new DuplicateTaxIdentificationNumberException("Tax identification number already exists");
        }
    }

    private BusinessVerification buildBusinessVerification(
            VerifyBusinessRequest request,
            DryCleanerEntity dryCleaner,
            String cacDocumentUrl) {

        BusinessVerification verification = new BusinessVerification();
        verification.setDryCleaner(dryCleaner);
        verification.setBusinessName(request.getBusinessName());
        verification.setBusinessType(request.getBusinessType());
        verification.setRegistrationNumber(request.getRegistrationNumber());
        verification.setTaxIdentificationNumber(request.getTaxIdentificationNumber());
        verification.setCacDocumentUrl(cacDocumentUrl);
        verification.setVerificationStatus(VerificationStatus.PENDING);
        verification.setSubmittedAt(LocalDateTime.now());
        return verification;
    }

    private VerifyBusinessResponse buildVerifyBusinessResponse(BusinessVerification verification) {
        VerifyBusinessResponse response = new VerifyBusinessResponse();
        response.setVerificationId(verification.getId());
        response.setDryCleanerId(verification.getDryCleaner().getId());
        response.setBusinessName(verification.getBusinessName());
        response.setBusinessType(verification.getBusinessType());
        response.setRegistrationNumber(verification.getRegistrationNumber());
        response.setVerificationStatus(verification.getVerificationStatus());
        response.setSubmittedAt(verification.getSubmittedAt());
        response.setMessage(BUSINESS_VERIFICATION_SUBMITTED_MESSAGE.getMessage());
        return response;
    }
}
