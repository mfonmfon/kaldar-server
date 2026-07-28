package com.kaldar.kaldar.shared.infrastructure.auth.api;

import com.kaldar.kaldar.shared.api.response.ApiResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SessionResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
@Tag(name = "Authentication", description = "Authentication and User Session Management")
public class SessionController {

    private final AuthenticationService authenticationService;

    public SessionController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping
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
}
