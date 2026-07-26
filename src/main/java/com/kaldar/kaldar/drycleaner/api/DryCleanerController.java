package com.kaldar.kaldar.drycleaner.api;

import com.kaldar.kaldar.drycleaner.application.dto.request.DryCleanerRegistrationRequest;
import com.kaldar.kaldar.drycleaner.application.dto.request.ServiceOfferingRequest;
import com.kaldar.kaldar.drycleaner.application.dto.request.UpdateDryCleanerProfileRequest;
import com.kaldar.kaldar.drycleaner.application.dto.request.UpdatePayoutAccountRequest;
import com.kaldar.kaldar.drycleaner.application.dto.request.UpdateWorkingHoursRequest;
import com.kaldar.kaldar.drycleaner.application.dto.request.VerifyBusinessRequest;
import com.kaldar.kaldar.drycleaner.application.dto.response.AnalyticsResponse;
import com.kaldar.kaldar.drycleaner.application.dto.response.DryCleanerProfileResponse;
import com.kaldar.kaldar.drycleaner.application.dto.response.OnboardingStatusResponse;
import com.kaldar.kaldar.drycleaner.application.dto.response.ServiceOfferingResponse;
import com.kaldar.kaldar.drycleaner.application.dto.response.VerifyBusinessResponse;
import com.kaldar.kaldar.drycleaner.application.service.BusinessVerificationService;
import com.kaldar.kaldar.drycleaner.application.service.DryCleanerService;
import com.kaldar.kaldar.shared.api.response.ApiResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.shared.infrastructure.storage.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.BUSINESS_VERIFICATION_SUBMITTED_MESSAGE;
import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.DRY_CLEANER_PROFILE_UPDATED_SUCCESS_MESSAGE;
import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.DRY_CLEANER_REGISTRATION_SUCCESS_MESSAGE;

@RestController
@RequestMapping("/api/v1/drycleaner")
public class DryCleanerController {

    private final DryCleanerService dryCleanerService;
    private final BusinessVerificationService businessVerificationService;
    private final FileStorageService fileStorageService;

    public DryCleanerController(DryCleanerService dryCleanerService,
                                BusinessVerificationService businessVerificationService,
                                FileStorageService fileStorageService) {
        this.dryCleanerService = dryCleanerService;
        this.businessVerificationService = businessVerificationService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/drycleaner/register")
    public ResponseEntity<ApiResponse<SendVerificationEmailResponse>> register(
            @RequestBody @Valid DryCleanerRegistrationRequest dryCleanerRegistrationRequest) {
        SendVerificationEmailResponse dryCleanerRegistrationResponse = dryCleanerService
                .registerDryCleaner(dryCleanerRegistrationRequest);
        ApiResponse<SendVerificationEmailResponse> apiResponse = ApiResponse
                .<SendVerificationEmailResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.CREATED.value())
                .message(DRY_CLEANER_REGISTRATION_SUCCESS_MESSAGE.getMessage())
                .data(dryCleanerRegistrationResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PatchMapping("/drycleaner/edit-profile")
    public ResponseEntity<ApiResponse<DryCleanerProfileResponse>> editProfile(
            @RequestBody @Valid UpdateDryCleanerProfileRequest dryCleanerProfileRequest) {
        DryCleanerProfileResponse dryCleanerProfileResponse = dryCleanerService
                .editProfile(dryCleanerProfileRequest);
        ApiResponse<DryCleanerProfileResponse> apiResponse = ApiResponse.<DryCleanerProfileResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(DRY_CLEANER_PROFILE_UPDATED_SUCCESS_MESSAGE.getMessage())
                .data(dryCleanerProfileResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping(value = "/drycleaner/verify-business", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<VerifyBusinessResponse>> verifyBusiness(
            @RequestPart("data") @Valid VerifyBusinessRequest request,
            @RequestPart("cacDocument") MultipartFile cacDocument) {
        String cacDocumentUrl = fileStorageService.uploadFile(cacDocument, "cac_documents");
        VerifyBusinessResponse response = businessVerificationService.submitBusinessVerification(request, cacDocumentUrl);
        ApiResponse<VerifyBusinessResponse> apiResponse = ApiResponse.<VerifyBusinessResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.CREATED.value())
                .message(BUSINESS_VERIFICATION_SUBMITTED_MESSAGE.getMessage())
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/drycleaner/{dryCleanerId}/verification-status")
    public ResponseEntity<ApiResponse<VerifyBusinessResponse>> getVerificationStatus(
            @PathVariable Long dryCleanerId) {
        VerifyBusinessResponse response = businessVerificationService.getVerificationStatus(dryCleanerId);
        ApiResponse<VerifyBusinessResponse> apiResponse = ApiResponse.<VerifyBusinessResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Verification status retrieved")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/drycleaner/{dryCleanerId}/services")
    public ResponseEntity<ApiResponse<List<ServiceOfferingResponse>>> getServices(
            @PathVariable Long dryCleanerId) {
        List<ServiceOfferingResponse> response = dryCleanerService.getServices(dryCleanerId);
        ApiResponse<List<ServiceOfferingResponse>> apiResponse = ApiResponse.<List<ServiceOfferingResponse>>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Services retrieved")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/drycleaner/{dryCleanerId}/services")
    public ResponseEntity<ApiResponse<ServiceOfferingResponse>> addOrUpdateService(
            @PathVariable Long dryCleanerId,
            @Valid @RequestBody ServiceOfferingRequest request) {
        ServiceOfferingResponse response = dryCleanerService.addOrUpdateService(dryCleanerId, request);
        ApiResponse<ServiceOfferingResponse> apiResponse = ApiResponse.<ServiceOfferingResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.CREATED.value())
                .message("Service offering added/updated")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/drycleaner/payout-account")
    public ResponseEntity<ApiResponse<Void>> updatePayoutAccount(
            @Valid @RequestBody UpdatePayoutAccountRequest request) {
        dryCleanerService.updatePayoutAccount(request.getDryCleanerId(), request.getAccountName(),
                request.getAccountNumber(), request.getBankCode(), request.getBankName());
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Payout account updated")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/drycleaner/working-hours")
    public ResponseEntity<ApiResponse<Void>> updateWorkingHours(
            @Valid @RequestBody UpdateWorkingHoursRequest request) {
        dryCleanerService.updateWorkingHours(request.getDryCleanerId(), request.getWorkingHoursJson());
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Working hours updated")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/drycleaner/{dryCleanerId}/onboarding-status")
    public ResponseEntity<ApiResponse<OnboardingStatusResponse>> getOnboardingStatus(
            @PathVariable Long dryCleanerId) {
        OnboardingStatusResponse response = dryCleanerService.getOnboardingStatus(dryCleanerId);
        ApiResponse<OnboardingStatusResponse> apiResponse = ApiResponse.<OnboardingStatusResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Onboarding status retrieved")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/drycleaner/{dryCleanerId}/analytics")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getAnalytics(
            @PathVariable Long dryCleanerId,
            @RequestParam(defaultValue = "today") String period) {
        AnalyticsResponse response = dryCleanerService.getAnalytics(dryCleanerId, period);
        ApiResponse<AnalyticsResponse> apiResponse = ApiResponse.<AnalyticsResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Analytics retrieved")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/drycleaner/{dryCleanerId}")
    public ResponseEntity<ApiResponse<Void>> deleteDryCleaner(@PathVariable Long dryCleanerId) {
        dryCleanerService.deleteDryCleaner(dryCleanerId);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Dry cleaner account deleted successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
