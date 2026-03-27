package com.kaldar.kaldar.shared.infrastructure.auth.api;

import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.AuthenticationRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.ForgotPasswordRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.ResetPasswordRequest;
import com.kaldar.kaldar.shared.api.response.ApiResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.AuthenticationResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.ForgotPasswordResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.ResetPasswordResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.service.AuthenticationService;
import com.kaldar.kaldar.shared.infrastructure.auth.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.AUTHENTICATION_SUCCESS_MESSAGE;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final PasswordResetService passwordResetService;

    public AuthenticationController(AuthenticationService authenticationService,
                                    PasswordResetService passwordResetService) {
        this.authenticationService = authenticationService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody AuthenticationRequest authenticationRequest){
        AuthenticationResponse authenticationResponse = authenticationService.login(authenticationRequest);
        ApiResponse<AuthenticationResponse> apiResponse = ApiResponse.<AuthenticationResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(AUTHENTICATION_SUCCESS_MESSAGE.getMessage())
                .data(authenticationResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordResponse response = passwordResetService.requestPasswordReset(request);
        ApiResponse<ForgotPasswordResponse> apiResponse = ApiResponse.<ForgotPasswordResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Password reset token sent")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse response = passwordResetService.resetPassword(request);
        ApiResponse<ResetPasswordResponse> apiResponse = ApiResponse.<ResetPasswordResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Password reset successful")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }



}
