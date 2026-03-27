package com.kaldar.kaldar.shared.infrastructure.auth.service.impl;

import com.kaldar.kaldar.shared.infrastructure.auth.domain.model.PasswordResetToken;
import com.kaldar.kaldar.shared.domain.model.UserEntity;
import com.kaldar.kaldar.shared.infrastructure.auth.domain.repository.PasswordResetTokenRepository;
import com.kaldar.kaldar.shared.domain.repository.UserEntityRepository;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.ForgotPasswordRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.ResetPasswordRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.ForgotPasswordResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.ResetPasswordResponse;
import com.kaldar.kaldar.shared.domain.exceptions.EmptyRequiredFieldException;
import com.kaldar.kaldar.shared.domain.exceptions.InvalidResetTokenException;
import com.kaldar.kaldar.shared.domain.exceptions.ResetTokenExpiredException;
import com.kaldar.kaldar.shared.domain.exceptions.ResetTokenNotFoundException;
import com.kaldar.kaldar.shared.domain.exceptions.UserNotFoundException;
import com.kaldar.kaldar.shared.infrastructure.email.service.EmailService;
import com.kaldar.kaldar.shared.infrastructure.auth.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
public class DefaultPasswordResetService implements PasswordResetService {
    private final UserEntityRepository userEntityRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final int expiryMinutes;
    private final String passwordResetBaseUrl;
    private final String passwordResetConfirmationUrl;

    public DefaultPasswordResetService(UserEntityRepository userEntityRepository,
                                       PasswordResetTokenRepository passwordResetTokenRepository,
                                       PasswordEncoder passwordEncoder,
                                       EmailService emailService,
                                       @Value("${security.password-reset.expiry-minutes:15}") int expiryMinutes,
                                       @Value("${app.password-reset.base-url:http://localhost:8080/reset-password.html}") String passwordResetBaseUrl,
                                       @Value("${app.password-reset.confirmation-url:http://localhost:8080/reset-password-confirmation.html}") String passwordResetConfirmationUrl) {
        this.userEntityRepository = userEntityRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.expiryMinutes = expiryMinutes;
        this.passwordResetBaseUrl = passwordResetBaseUrl;
        this.passwordResetConfirmationUrl = passwordResetConfirmationUrl;
    }

    @Override
    public ForgotPasswordResponse requestPasswordReset(ForgotPasswordRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank()) {
            throw new EmptyRequiredFieldException("Email is required");
        }
        UserEntity user = userEntityRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));

        passwordResetTokenRepository.findByUserEntityAndUsedAtIsNull(user)
                .ifPresent(token -> {
                    token.setUsedAt(LocalDateTime.now());
                    passwordResetTokenRepository.save(token);
                });

        String rawToken = UUID.randomUUID().toString().replace("-", "");
        String hashedToken = passwordEncoder.encode(rawToken);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUserEntity(user);
        resetToken.setToken(hashedToken);
        resetToken.setExpiredAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        passwordResetTokenRepository.save(resetToken);

        String resetUrl = buildResetUrl(user.getEmail(), rawToken);
        emailService.sendPasswordResetEmail(user.getEmail(), rawToken, resetUrl);

        ForgotPasswordResponse response = new ForgotPasswordResponse();
        response.setEmail(user.getEmail());
        response.setMessage("Password reset token sent");
        response.setExpiresAt(resetToken.getExpiredAt().toString());
        response.setResetUrl(resetUrl);
        return response;
    }

    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        if (request == null) {
            throw new EmptyRequiredFieldException("Reset request is required");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new EmptyRequiredFieldException("New password is required");
        }
        UserEntity user = userEntityRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));

        PasswordResetToken token = passwordResetTokenRepository.findByUserEntityAndUsedAtIsNull(user)
                .orElseThrow(() -> new ResetTokenNotFoundException("Reset token not found"));

        if (LocalDateTime.now().isAfter(token.getExpiredAt())) {
            throw new ResetTokenExpiredException("Reset token has expired");
        }
        if (!passwordEncoder.matches(request.getToken(), token.getToken())) {
            throw new InvalidResetTokenException("Invalid reset token");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userEntityRepository.save(user);

        token.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(token);

        ResetPasswordResponse response = new ResetPasswordResponse();
        response.setMessage("Password reset successful");
        response.setConfirmationUrl(passwordResetConfirmationUrl);
        return response;
    }

    private String buildResetUrl(String email, String token) {
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        if (passwordResetBaseUrl.contains("?")) {
            return passwordResetBaseUrl + "&email=" + encodedEmail + "&token=" + encodedToken;
        }
        return passwordResetBaseUrl + "?email=" + encodedEmail + "&token=" + encodedToken;
    }
}

