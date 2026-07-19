package com.kaldar.kaldar.drycleaner.application.service;

import com.kaldar.kaldar.drycleaner.application.dto.request.DryCleanerRegistrationRequest;
import com.kaldar.kaldar.drycleaner.application.dto.request.UpdateDryCleanerProfileRequest;
import com.kaldar.kaldar.drycleaner.application.dto.response.AnalyticsResponse;
import com.kaldar.kaldar.drycleaner.application.dto.response.DryCleanerProfileResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SendVerificationEmailResponse;

public interface DryCleanerService {

    SendVerificationEmailResponse registerDryCleaner(DryCleanerRegistrationRequest dryCleanerRegistrationRequest);

    DryCleanerProfileResponse editProfile(UpdateDryCleanerProfileRequest updateDryCleanerProfileRequest);

    java.util.List<com.kaldar.kaldar.drycleaner.application.dto.response.ServiceOfferingResponse> getServices(Long dryCleanerId);

    com.kaldar.kaldar.drycleaner.application.dto.response.ServiceOfferingResponse addOrUpdateService(Long dryCleanerId, com.kaldar.kaldar.drycleaner.application.dto.request.ServiceOfferingRequest request);

    void updatePayoutAccount(Long dryCleanerId, String accountName, String accountNumber, String bankCode, String bankName);

    void updateWorkingHours(Long dryCleanerId, String workingHoursJson);

    com.kaldar.kaldar.drycleaner.application.dto.response.OnboardingStatusResponse getOnboardingStatus(Long dryCleanerId);

    com.kaldar.kaldar.drycleaner.application.dto.response.AnalyticsResponse getAnalytics(Long dryCleanerId, String period);

    AnalyticsResponse findDrycleanerByBusinessName(String businessName);

    DryCleanerProfileResponse getProfile();

}
