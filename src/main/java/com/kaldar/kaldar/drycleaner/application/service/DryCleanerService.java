package com.kaldar.kaldar.drycleaner.application.service;

import com.kaldar.kaldar.drycleaner.application.dto.request.DryCleanerRegistrationRequest;
import com.kaldar.kaldar.drycleaner.application.dto.request.ServiceOfferingRequest;
import com.kaldar.kaldar.drycleaner.application.dto.request.UpdateDryCleanerProfileRequest;
import com.kaldar.kaldar.drycleaner.application.dto.response.AnalyticsResponse;
import com.kaldar.kaldar.drycleaner.application.dto.response.DryCleanerProfileResponse;
import com.kaldar.kaldar.drycleaner.application.dto.response.OnboardingStatusResponse;
import com.kaldar.kaldar.drycleaner.application.dto.response.ServiceOfferingResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SendVerificationEmailResponse;

import java.util.List;

public interface DryCleanerService {

    SendVerificationEmailResponse registerDryCleaner(DryCleanerRegistrationRequest dryCleanerRegistrationRequest);

    DryCleanerProfileResponse editProfile(UpdateDryCleanerProfileRequest updateDryCleanerProfileRequest);

    List<ServiceOfferingResponse> getServices(Long dryCleanerId);

    ServiceOfferingResponse addOrUpdateService(Long dryCleanerId, ServiceOfferingRequest request);

    void updatePayoutAccount(Long dryCleanerId, String accountName, String accountNumber, String bankCode, String bankName);

    void updateWorkingHours(Long dryCleanerId, String workingHoursJson);

    OnboardingStatusResponse getOnboardingStatus(Long dryCleanerId);

    AnalyticsResponse getAnalytics(Long dryCleanerId, String period);

    AnalyticsResponse findDrycleanerByBusinessName(String businessName);

    DryCleanerProfileResponse getProfile();

    void deleteDryCleaner(Long dryCleanerId);
}
