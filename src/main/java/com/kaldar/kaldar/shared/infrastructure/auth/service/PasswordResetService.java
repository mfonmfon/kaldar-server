package com.kaldar.kaldar.shared.infrastructure.auth.service;

import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.ForgotPasswordRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.ResetPasswordRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.ForgotPasswordResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.ResetPasswordResponse;

public interface PasswordResetService {
    ForgotPasswordResponse requestPasswordReset(ForgotPasswordRequest request);

    ResetPasswordResponse resetPassword(ResetPasswordRequest request);
}

