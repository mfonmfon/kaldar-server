package com.kaldar.kaldar.auth.service;

import com.kaldar.kaldar.shared.infrastructure.auth.domain.model.PasswordResetToken;
import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.shared.domain.model.UserEntity;
import com.kaldar.kaldar.shared.infrastructure.auth.domain.repository.PasswordResetTokenRepository;
import com.kaldar.kaldar.shared.domain.repository.UserEntityRepository;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.ForgotPasswordRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.ResetPasswordRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.ForgotPasswordResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.ResetPasswordResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.service.impl.DefaultPasswordResetService;
import com.kaldar.kaldar.shared.domain.exceptions.*;
import com.kaldar.kaldar.shared.infrastructure.email.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultPasswordResetService Unit Tests")
class DefaultPasswordResetServiceTest {

    @Mock private UserEntityRepository userEntityRepository;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    private DefaultPasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        passwordResetService = new DefaultPasswordResetService(
                userEntityRepository, passwordResetTokenRepository, passwordEncoder,
                emailService, 15,
                "http://localhost:8080/reset-password.html",
                "http://localhost:8080/reset-confirmation.html"
        );
    }

    private CustomerEntity buildUser() {
        CustomerEntity user = new CustomerEntity();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("$2a$encoded");
        return user;
    }

    // =========================================================================
    // requestPasswordReset (Forgot Password)
    // =========================================================================

    @Nested
    @DisplayName("requestPasswordReset()")
    class RequestPasswordReset {

        @Test
        @DisplayName("should generate reset token and send email successfully")
        void shouldSendResetTokenSuccessfully() {
            UserEntity user = buildUser();
            when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
            when(passwordResetTokenRepository.findByUserEntityAndUsedAtIsNull(user)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$hashedToken");
            when(passwordResetTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            ForgotPasswordRequest request = new ForgotPasswordRequest();
            request.setEmail("user@example.com");

            ForgotPasswordResponse response = passwordResetService.requestPasswordReset(request);

            assertThat(response.getEmail()).isEqualTo("user@example.com");
            assertThat(response.getMessage()).contains("reset token sent");
            assertThat(response.getExpiresAt()).isNotNull();
            assertThat(response.getResetUrl()).contains("user%40example.com");

            verify(emailService).sendPasswordResetEmail(eq("user@example.com"), anyString(), anyString());
            verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        }

        @Test
        @DisplayName("should invalidate old token before creating a new one")
        void shouldInvalidateOldTokenBeforeCreatingNew() {
            UserEntity user = buildUser();
            PasswordResetToken oldToken = new PasswordResetToken();
            oldToken.setUserEntity(user);
            oldToken.setToken("$2a$oldHash");
            oldToken.setExpiredAt(LocalDateTime.now().plusMinutes(10));

            when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
            when(passwordResetTokenRepository.findByUserEntityAndUsedAtIsNull(user))
                    .thenReturn(Optional.of(oldToken));
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$newHash");
            when(passwordResetTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            ForgotPasswordRequest request = new ForgotPasswordRequest();
            request.setEmail("user@example.com");

            passwordResetService.requestPasswordReset(request);

            // Old token should be marked as used
            assertThat(oldToken.getUsedAt()).isNotNull();
            verify(passwordResetTokenRepository, times(2)).save(any()); // once for old, once for new
        }

        @Test
        @DisplayName("should throw EmptyRequiredFieldException when email is blank")
        void shouldThrowWhenEmailIsBlank() {
            ForgotPasswordRequest request = new ForgotPasswordRequest();
            request.setEmail("  ");

            assertThatThrownBy(() -> passwordResetService.requestPasswordReset(request))
                    .isInstanceOf(EmptyRequiredFieldException.class)
                    .hasMessageContaining("Email is required");
        }

        @Test
        @DisplayName("should throw EmptyRequiredFieldException when request is null")
        void shouldThrowWhenRequestIsNull() {
            assertThatThrownBy(() -> passwordResetService.requestPasswordReset(null))
                    .isInstanceOf(EmptyRequiredFieldException.class);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when email is not registered")
        void shouldThrowWhenUserNotFound() {
            when(userEntityRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

            ForgotPasswordRequest request = new ForgotPasswordRequest();
            request.setEmail("unknown@example.com");

            assertThatThrownBy(() -> passwordResetService.requestPasswordReset(request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    // =========================================================================
    // resetPassword
    // =========================================================================

    @Nested
    @DisplayName("resetPassword()")
    class ResetPassword {

        @Test
        @DisplayName("should reset password successfully with valid token")
        void shouldResetPasswordSuccessfully() {
            UserEntity user = buildUser();
            PasswordResetToken token = new PasswordResetToken();
            token.setUserEntity(user);
            token.setToken("$2a$hashedToken");
            token.setExpiredAt(LocalDateTime.now().plusMinutes(10));

            when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
            when(passwordResetTokenRepository.findByUserEntityAndUsedAtIsNull(user))
                    .thenReturn(Optional.of(token));
            when(passwordEncoder.matches("rawToken123", "$2a$hashedToken")).thenReturn(true);
            when(passwordEncoder.encode("NewPassword123!")).thenReturn("$2a$newHashed");
            when(userEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(passwordResetTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail("user@example.com");
            request.setToken("rawToken123");
            request.setNewPassword("NewPassword123!");

            ResetPasswordResponse response = passwordResetService.resetPassword(request);

            assertThat(response.getMessage()).isEqualTo("Password reset successful");
            assertThat(response.getConfirmationUrl()).isNotNull();

            ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userEntityRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword()).isEqualTo("$2a$newHashed");

            // Token should be marked as used
            assertThat(token.getUsedAt()).isNotNull();
        }

        @Test
        @DisplayName("should throw ResetTokenExpiredException when token has expired")
        void shouldThrowWhenTokenExpired() {
            UserEntity user = buildUser();
            PasswordResetToken token = new PasswordResetToken();
            token.setToken("$2a$hashedToken");
            token.setExpiredAt(LocalDateTime.now().minusMinutes(5)); // expired

            when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
            when(passwordResetTokenRepository.findByUserEntityAndUsedAtIsNull(user))
                    .thenReturn(Optional.of(token));

            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail("user@example.com");
            request.setToken("rawToken123");
            request.setNewPassword("NewPassword123!");

            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                    .isInstanceOf(ResetTokenExpiredException.class)
                    .hasMessageContaining("expired");
        }

        @Test
        @DisplayName("should throw InvalidResetTokenException when token does not match")
        void shouldThrowWhenTokenIsInvalid() {
            UserEntity user = buildUser();
            PasswordResetToken token = new PasswordResetToken();
            token.setToken("$2a$hashedToken");
            token.setExpiredAt(LocalDateTime.now().plusMinutes(10));

            when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
            when(passwordResetTokenRepository.findByUserEntityAndUsedAtIsNull(user))
                    .thenReturn(Optional.of(token));
            when(passwordEncoder.matches("wrongToken", "$2a$hashedToken")).thenReturn(false);

            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail("user@example.com");
            request.setToken("wrongToken");
            request.setNewPassword("NewPassword123!");

            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                    .isInstanceOf(InvalidResetTokenException.class)
                    .hasMessageContaining("Invalid reset token");
        }

        @Test
        @DisplayName("should throw ResetTokenNotFoundException when no active token exists")
        void shouldThrowWhenNoActiveToken() {
            UserEntity user = buildUser();
            when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
            when(passwordResetTokenRepository.findByUserEntityAndUsedAtIsNull(user))
                    .thenReturn(Optional.empty());

            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail("user@example.com");
            request.setToken("anyToken");
            request.setNewPassword("NewPassword123!");

            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                    .isInstanceOf(ResetTokenNotFoundException.class);
        }

        @Test
        @DisplayName("should throw EmptyRequiredFieldException when new password is blank")
        void shouldThrowWhenNewPasswordIsBlank() {
            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail("user@example.com");
            request.setToken("someToken");
            request.setNewPassword("  ");

            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                    .isInstanceOf(EmptyRequiredFieldException.class)
                    .hasMessageContaining("New password is required");
        }
    }
}
