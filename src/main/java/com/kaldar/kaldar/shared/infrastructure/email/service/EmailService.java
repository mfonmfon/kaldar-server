package com.kaldar.kaldar.shared.infrastructure.email.service;

import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.SendVerificationEmailRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SendVerificationEmailResponse;


public interface EmailService {

    SendVerificationEmailResponse sendVerificationEmail(String email, String otpDigitNumberGenerator);

    void sendPasswordResetEmail(String email, String resetToken, String resetUrl);
}
