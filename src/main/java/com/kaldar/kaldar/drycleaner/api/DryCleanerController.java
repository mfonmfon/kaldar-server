package com.kaldar.kaldar.drycleaner.api;
import com.kaldar.kaldar.order.application.dto.request.AcceptOrderRequest;
import com.kaldar.kaldar.order.application.dto.response.AcceptOrderResponse;
import com.kaldar.kaldar.drycleaner.application.dto.request.DryCleanerRegistrationRequest;
import com.kaldar.kaldar.drycleaner.application.dto.request.UpdateDryCleanerProfileRequest;
import com.kaldar.kaldar.shared.api.response.ApiResponse;
import com.kaldar.kaldar.drycleaner.application.dto.response.DryCleanerProfileResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.drycleaner.application.service.DryCleanerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.*;

@RestController
@RequestMapping("/api/v1/auth")
public class DryCleanerController {


    private final DryCleanerService dryCleanerService;

    public DryCleanerController(DryCleanerService dryCleanerService) {
        this.dryCleanerService = dryCleanerService;
    }

    @PostMapping("/drycleaner/register")
    public ResponseEntity<ApiResponse<SendVerificationEmailResponse>> register(
            @RequestBody @Valid DryCleanerRegistrationRequest dryCleanerRegistrationRequest){
        SendVerificationEmailResponse dryCleanerRegistrationResponse = dryCleanerService.registerDryCleaner(dryCleanerRegistrationRequest);
        ApiResponse<SendVerificationEmailResponse> apiResponse = ApiResponse.<SendVerificationEmailResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.CREATED.value())
                .message(DRY_CLEANER_REGISTRATION_SUCCESS_MESSAGE.getMessage())
                .data(dryCleanerRegistrationResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PatchMapping("/drycleaner/edit-profile")
    public ResponseEntity<ApiResponse<DryCleanerProfileResponse>> editProfile(@RequestBody @Valid UpdateDryCleanerProfileRequest dryCleanerProfileRequest){
        DryCleanerProfileResponse dryCleanerProfileResponse = dryCleanerService.editProfile(dryCleanerProfileRequest);
        ApiResponse<DryCleanerProfileResponse> apiResponse = ApiResponse.<DryCleanerProfileResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(DRY_CLEANER_PROFILE_UPDATED_SUCCESS_MESSAGE.getMessage())
                .data(dryCleanerProfileResponse)
                .build();
        return  ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
