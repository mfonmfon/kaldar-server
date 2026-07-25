package com.kaldar.kaldar.auth.service;

import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.shared.domain.model.UserEntity;
import com.kaldar.kaldar.shared.infrastructure.auth.domain.model.VerificationToken;
import com.kaldar.kaldar.shared.infrastructure.auth.domain.repository.VerificationTokenRepository;
import com.kaldar.kaldar.shared.domain.repository.UserEntityRepository;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.ResendOtpRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.VerifyOtpRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.VerifyOtpResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.service.impl.DefaultVerificationTokenService;
import com.kaldar.kaldar.shared.domain.exceptions.*;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SendVerificationEmailResponse;
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
@DisplayName("DefaultVerificationTokenService Unit Tests")
class DefaultVerificationTokenServiceTest {

        @Mock
        private VerificationTokenRepository verificationTokenRepository;
        @Mock
        private PasswordEncoder passwordEncoder;
        @Mock
        private UserEntityRepository userEntityRepository;
        @Mock
        private EmailService emailService;
        @Mock
        private com.kaldar.kaldar.wallet.application.service.WalletService walletService;

        private DefaultVerificationTokenService verificationTokenService;

        @BeforeEach
        void setUp() {
                verificationTokenService = new DefaultVerificationTokenService(
                                verificationTokenRepository, passwordEncoder, userEntityRepository,
                                emailService, walletService, 15);
        }

        private CustomerEntity buildUser(boolean verified) {
                CustomerEntity user = new CustomerEntity();
                user.setId(1L);
                user.setEmail("user@example.com");
                user.setVerifiedUser(verified);
                return user;
        }

        private VerificationToken buildToken(UserEntity user, boolean expired) {
                VerificationToken token = new VerificationToken();
                token.setToken("$2a$hashedOtp");
                token.setUserEntity(user);
                token.setExpiredAt(expired
                                ? LocalDateTime.now().minusMinutes(5)
                                : LocalDateTime.now().plusMinutes(15));
                return token;
        }

        // =========================================================================
        // verifyOtp
        // =========================================================================

        @Nested
        @DisplayName("verifyOtp()")
        class VerifyOtp {

                @Test
                @DisplayName("should verify OTP successfully and mark user as verified")
                void shouldVerifyOtpSuccessfully() {
                        UserEntity user = buildUser(false);
                        VerificationToken token = buildToken(user, false);

                        when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
                        when(verificationTokenRepository.findByUserEntityAndUsedAtIsNull(user))
                                        .thenReturn(Optional.of(token));
                        when(passwordEncoder.matches("123456", "$2a$hashedOtp")).thenReturn(true);
                        when(userEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                        when(verificationTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

                        VerifyOtpRequest request = new VerifyOtpRequest();
                        request.setEmail("user@example.com");
                        request.setOtpInput("123456");

                        VerifyOtpResponse response = verificationTokenService.verifyOtp(request);

                        assertThat(response.getEmail()).isEqualTo("user@example.com");
                        assertThat(response.getOtpVerificationMessage()).isNotBlank();
                        assertThat(response.getVerifiedAt()).isNotNull();

                        // User must be marked as verified
                        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
                        verify(userEntityRepository).save(userCaptor.capture());
                        assertThat(userCaptor.getValue().isVerifiedUser()).isTrue();

                        // Token must be marked as used
                        ArgumentCaptor<VerificationToken> tokenCaptor = ArgumentCaptor
                                        .forClass(VerificationToken.class);
                        verify(verificationTokenRepository).save(tokenCaptor.capture());
                        assertThat(tokenCaptor.getValue().getUsedAt()).isNotNull();
                }

                @Test
                @DisplayName("should throw UserAlreadyVerifiedException when user is already verified")
                void shouldThrowWhenUserAlreadyVerified() {
                        UserEntity user = buildUser(true); // already verified
                        VerificationToken token = buildToken(user, false);

                        when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
                        when(verificationTokenRepository.findByUserEntityAndUsedAtIsNull(user))
                                        .thenReturn(Optional.of(token));

                        VerifyOtpRequest request = new VerifyOtpRequest();
                        request.setEmail("user@example.com");
                        request.setOtpInput("123456");

                        assertThatThrownBy(() -> verificationTokenService.verifyOtp(request))
                                        .isInstanceOf(UserAlreadyVerifiedException.class);
                }

                @Test
                @DisplayName("should throw ExpiredOtpException when token is expired")
                void shouldThrowWhenOtpExpired() {
                        UserEntity user = buildUser(false);
                        VerificationToken token = buildToken(user, true); // expired

                        when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
                        when(verificationTokenRepository.findByUserEntityAndUsedAtIsNull(user))
                                        .thenReturn(Optional.of(token));

                        VerifyOtpRequest request = new VerifyOtpRequest();
                        request.setEmail("user@example.com");
                        request.setOtpInput("123456");

                        assertThatThrownBy(() -> verificationTokenService.verifyOtp(request))
                                        .isInstanceOf(ExpiredOtpException.class);
                }

                @Test
                @DisplayName("should throw InvalidOtpException when OTP does not match")
                void shouldThrowWhenOtpIsInvalid() {
                        UserEntity user = buildUser(false);
                        VerificationToken token = buildToken(user, false);

                        when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
                        when(verificationTokenRepository.findByUserEntityAndUsedAtIsNull(user))
                                        .thenReturn(Optional.of(token));
                        when(passwordEncoder.matches("wrongOtp", "$2a$hashedOtp")).thenReturn(false);

                        VerifyOtpRequest request = new VerifyOtpRequest();
                        request.setEmail("user@example.com");
                        request.setOtpInput("wrongOtp");

                        assertThatThrownBy(() -> verificationTokenService.verifyOtp(request))
                                        .isInstanceOf(InvalidOtpException.class);
                }

                @Test
                @DisplayName("should throw OTPNotFoundException when no active OTP token exists")
                void shouldThrowWhenNoActiveOtp() {
                        UserEntity user = buildUser(false);
                        when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
                        when(verificationTokenRepository.findByUserEntityAndUsedAtIsNull(user))
                                        .thenReturn(Optional.empty());

                        VerifyOtpRequest request = new VerifyOtpRequest();
                        request.setEmail("user@example.com");
                        request.setOtpInput("123456");

                        assertThatThrownBy(() -> verificationTokenService.verifyOtp(request))
                                        .isInstanceOf(OTPNotFoundException.class);
                }

                @Test
                @DisplayName("should throw UserNotFoundException when email does not exist")
                void shouldThrowWhenUserNotFound() {
                        when(userEntityRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

                        VerifyOtpRequest request = new VerifyOtpRequest();
                        request.setEmail("unknown@example.com");
                        request.setOtpInput("123456");

                        assertThatThrownBy(() -> verificationTokenService.verifyOtp(request))
                                        .isInstanceOf(UserNotFoundException.class);
                }
        }

        // =========================================================================
        // resendOtp
        // =========================================================================

        @Nested
        @DisplayName("resendOtp()")
        class ResendOtp {

                @Test
                @DisplayName("should resend OTP and create a new token for the user")
                void shouldResendOtpSuccessfully() {
                        UserEntity user = buildUser(false);
                        when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
                        when(verificationTokenRepository.findByUserEntityAndUsedAtIsNull(user))
                                        .thenReturn(Optional.empty());
                        when(passwordEncoder.encode(anyString())).thenReturn("$2a$newHashedOtp");
                        when(verificationTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                        when(emailService.sendVerificationEmail(anyString(), anyString()))
                                        .thenReturn(new SendVerificationEmailResponse());

                        ResendOtpRequest request = new ResendOtpRequest();
                        request.setEmail("user@example.com");

                        VerifyOtpResponse response = verificationTokenService.resendOtp(request);

                        assertThat(response.getEmail()).isEqualTo("user@example.com");
                        assertThat(response.getOtpVerificationMessage()).isNotBlank();
                        verify(emailService).sendVerificationEmail(eq("user@example.com"), anyString());
                        verify(verificationTokenRepository).save(any(VerificationToken.class));
                }

                @Test
                @DisplayName("should invalidate existing OTP before creating a new one")
                void shouldInvalidateExistingOtpBeforeResending() {
                        UserEntity user = buildUser(false);
                        VerificationToken oldToken = buildToken(user, false);

                        when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
                        when(verificationTokenRepository.findByUserEntityAndUsedAtIsNull(user))
                                        .thenReturn(Optional.of(oldToken));
                        when(passwordEncoder.encode(anyString())).thenReturn("$2a$newHashedOtp");
                        when(verificationTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                        when(emailService.sendVerificationEmail(anyString(), anyString()))
                                        .thenReturn(new SendVerificationEmailResponse());

                        ResendOtpRequest request = new ResendOtpRequest();
                        request.setEmail("user@example.com");

                        verificationTokenService.resendOtp(request);

                        // Old token must be marked as used
                        assertThat(oldToken.getUsedAt()).isNotNull();
                }

                @Test
                @DisplayName("should throw UserNotFoundException when email does not exist")
                void shouldThrowWhenUserNotFound() {
                        when(userEntityRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

                        ResendOtpRequest request = new ResendOtpRequest();
                        request.setEmail("unknown@example.com");

                        assertThatThrownBy(() -> verificationTokenService.resendOtp(request))
                                        .isInstanceOf(UserNotFoundException.class);
                }
        }
}
