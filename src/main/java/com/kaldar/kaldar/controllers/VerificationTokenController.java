package com.kaldar.kaldar.controllers;
import com.kaldar.kaldar.dtos.request.ResendOtpRequest;
import com.kaldar.kaldar.dtos.request.VerifyOtpRequest;
import com.kaldar.kaldar.dtos.response.ApiResponse;
import com.kaldar.kaldar.dtos.response.VerifyOtpResponse;
import com.kaldar.kaldar.kaldarService.interfaces.VerificationTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.kaldar.kaldar.contants.StatusResponse.VERIFICATION_OTP_SUCCESS_MESSAGE;

@RestController
@RequestMapping("/api/v1/auth/verify")
public class VerificationTokenController {

    private final VerificationTokenService verificationTokenService;

    public VerificationTokenController(VerificationTokenService verificationTokenService) {
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<VerifyOtpResponse>> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest){
        VerifyOtpResponse verifyOtpResponse = verificationTokenService.verifyOtp(verifyOtpRequest);
        ApiResponse<VerifyOtpResponse> apiResponse = ApiResponse.<VerifyOtpResponse>
                builder()
                .status(HttpStatus.OK.value())
                .data(verifyOtpResponse)
                .isSuccess(true)
                .message(VERIFICATION_OTP_SUCCESS_MESSAGE.getMessage())
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/resendOtp")
    public ResponseEntity<ApiResponse<VerifyOtpResponse>> resendOtp(@RequestBody ResendOtpRequest resendOtpRequest){
        VerifyOtpResponse verifyOtpResponse = verificationTokenService.resendOtp(resendOtpRequest);
        ApiResponse<VerifyOtpResponse> apiResponse = ApiResponse.<VerifyOtpResponse>builder()
                .status(HttpStatus.OK.value())
                .data(verifyOtpResponse)
                .isSuccess(true)
                .message(VERIFICATION_OTP_SUCCESS_MESSAGE.getMessage())
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }


}
