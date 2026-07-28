package com.kaldar.kaldar.shared.infrastructure.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthenticationRequest {
    @Schema(description = "Registered user email address", example = "customer@kaldar.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "Must be a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "User account password", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password is required")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
