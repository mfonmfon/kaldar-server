package com.kaldar.kaldar.shared.infrastructure.auth.api;

import com.kaldar.kaldar.shared.api.response.ApiResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.AuthenticationRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.ForgotPasswordRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.ResetPasswordRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.AuthenticationResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.ForgotPasswordResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.ResetPasswordResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SessionResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.service.AuthenticationService;
import com.kaldar.kaldar.shared.infrastructure.auth.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.AUTHENTICATION_SUCCESS_MESSAGE;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and User Session Management")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final PasswordResetService passwordResetService;

    public AuthenticationController(AuthenticationService authenticationService,
                                    PasswordResetService passwordResetService) {
        this.authenticationService = authenticationService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates user credentials and returns JWT bearer token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@Valid @RequestBody AuthenticationRequest authenticationRequest){
        AuthenticationResponse authenticationResponse = authenticationService.login(authenticationRequest);
        ApiResponse<AuthenticationResponse> apiResponse = ApiResponse.<AuthenticationResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(AUTHENTICATION_SUCCESS_MESSAGE.getMessage())
                .data(authenticationResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping({"/session", "/session-info"})
    @Operation(summary = "Get Authenticated Session", description = "Returns details of the currently authenticated user, including customerId or dryCleanerId")
    public ResponseEntity<ApiResponse<SessionResponse>> getSession() {
        SessionResponse sessionResponse = authenticationService.getSessionInfo();
        ApiResponse<SessionResponse> apiResponse = ApiResponse.<SessionResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Session details retrieved successfully")
                .data(sessionResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot Password Request", description = "Sends password reset token to registered email")
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
    @Operation(summary = "Reset Password", description = "Resets user password using reset token")
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
