package com.kaldar.kaldar.shared.infrastructure.email.service.impl;

import com.kaldar.kaldar.shared.domain.exceptions.EmailSendException;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.shared.infrastructure.email.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class DefaultEmailService implements EmailService {

    @Value("${spring.mail.from-name}")
    private String fromName;

    @Value("${spring.mail.from-email}")
    private String fromEmail;

    private final Mailer primaryMailer;
    private final Mailer fallbackMailer;

    @Value("${security.otp.expiry-minutes}")
    private int otpExpiryMinutes;

    @Value("${security.password-reset.expiry-minutes:15}")
    private int passwordResetExpiryMinutes;

    @Value("${app.password-reset.base-url:http://localhost:8080/reset-password.html}")
    private String passwordResetBaseUrl;

    public DefaultEmailService(@Qualifier("primaryMailer") Mailer primaryMailer,
                               @Qualifier("fallbackMailer") Mailer fallbackMailer) {
        this.primaryMailer = primaryMailer;
        this.fallbackMailer = fallbackMailer;
    }

    @Override
    public SendVerificationEmailResponse sendVerificationEmail(String recipientEmail, String otpDigitNumberGenerator) {
        Email email = EmailBuilder.startingBlank()
                .from(fromName, fromEmail)
                .to(recipientEmail)
                .withSubject("Your Verification Code")
                .withPlainText("Your verification code is: " + otpDigitNumberGenerator +
                        "\n\nIt expires in " + otpExpiryMinutes + " minutes.")
                .withHTMLText(
                        "<h2 style='color:#2c3e50;'>Verification Code</h2>" +
                                "<p>Your OTP is: <b style='font-size:18px;'>" +
                                otpDigitNumberGenerator + "</b></p>" +
                                "<p>This code will expire in <b>" +
                                otpExpiryMinutes + "</b> minutes.</p>")
                .buildEmail();

        sendWithFallback(email);
        return new SendVerificationEmailResponse(recipientEmail, "Verification code sent successfully", java.time.LocalDateTime.now().plusMinutes(otpExpiryMinutes).toString());
    }

    @Override
    public void sendPasswordResetEmail(String recipientEmail, String resetToken, String resetUrl) {
        String link = resetUrl == null || resetUrl.isBlank()
                ? buildResetUrl(recipientEmail, resetToken)
                : resetUrl;
        Email email = EmailBuilder.startingBlank()
                .from(fromName, fromEmail)
                .to(recipientEmail)
                .withSubject("Password Reset")
                .withPlainText("Use this token to reset your password: " + resetToken +
                        "\nReset link: " + link +
                        "\n\nIt expires in " + passwordResetExpiryMinutes + " minutes.")
                .withHTMLText(buildPasswordResetHtml(resetToken, link))
                .buildEmail();

        sendWithFallback(email);
    }

    private void sendWithFallback(Email email) {
        try {
            primaryMailer.sendMail(email);
        } catch (Exception primaryEx) {
            log.warn("Primary SMTP mail delivery failed: {}. Attempting fallback port...", primaryEx.getMessage());
            try {
                fallbackMailer.sendMail(email);
                log.info("Email successfully delivered using fallback SMTP port.");
            } catch (Exception fallbackEx) {
                log.error("Fallback SMTP mail delivery failed: {}", fallbackEx.getMessage(), fallbackEx);
                throw new EmailSendException("Failed to send email via both primary and fallback SMTP ports: " + fallbackEx.getMessage());
            }
        }
    }

    private String buildPasswordResetHtml(String resetToken, String resetUrl) {
        return "<div style='font-family:Arial,sans-serif;line-height:1.6;color:#2c3e50;'>" +
                "<h2>Password Reset</h2>" +
                "<p>Use the token below to reset your password:</p>" +
                "<p style='font-size:18px;font-weight:bold;'>" + resetToken + "</p>" +
                "<p><a href='" + resetUrl + "'>Click here to reset your password</a></p>" +
                "<p>This token expires in <b>" + passwordResetExpiryMinutes + "</b> minutes.</p>" +
                "</div>";
    }

    private String buildResetUrl(String email, String token) {
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        if (passwordResetBaseUrl.contains("?")) {
            return passwordResetBaseUrl + "&email=" + encodedEmail + "&token=" + encodedToken;
        }
        return passwordResetBaseUrl + "?email=" + encodedEmail + "?token=" + encodedToken;
    }
}