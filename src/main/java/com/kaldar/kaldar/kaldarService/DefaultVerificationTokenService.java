package com.kaldar.kaldar.kaldarService;

import com.kaldar.kaldar.domain.entities.UserEntity;
import com.kaldar.kaldar.domain.entities.VerificationToken;
import com.kaldar.kaldar.domain.repository.UserEntityRepository;
import com.kaldar.kaldar.domain.repository.VerificationTokenRepository;
import com.kaldar.kaldar.dtos.request.ResendOtpRequest;
import com.kaldar.kaldar.dtos.request.VerifyOtpRequest;
import com.kaldar.kaldar.dtos.response.VerifyOtpResponse;
import com.kaldar.kaldar.exceptions.*;
import com.kaldar.kaldar.utility.OtpGenerator;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.kaldar.kaldar.contants.StatusResponse.*;

@Service
public class DefaultVerificationTokenService implements VerificationTokenService{
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityRepository userEntityRepository;
    private final EmailService emailService;

    public DefaultVerificationTokenService(VerificationTokenRepository verificationTokenRepository,
                                           PasswordEncoder passwordEncoder,
                                           UserEntityRepository userEntityRepository,
                                           EmailService emailService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.userEntityRepository = userEntityRepository;
        this.emailService = emailService;
    }

    @Override
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest verifyOtpRequest) {
        UserEntity userEntity = userEntityRepository.findByEmail(verifyOtpRequest.getEmail())
                .orElseThrow(()-> new UserNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        VerificationToken verificationToken = verificationTokenRepository.findByUserEntityAndUsedAtIsNull(userEntity)
                .orElseThrow(()-> new OTPNotFoundException(OTP_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        if (userEntity.isVerifiedUser())
            throw new UserAlreadyVerifiedException(USER_ALREADY_VERIFIED_MESSAGE.getMessage());
        if (verificationToken.getExpiredAt().isBefore(LocalDateTime.now()))
            throw new ExpiredOtpException(EXPIRED_OTP_EXCEPTION_MESSAGE.getMessage());
        if (passwordEncoder.matches(verifyOtpRequest.getOtpInput(), verificationToken.getToken()))
            throw new InvalidOtpException(INVALID_OTP_EXCEPTION.getMessage());
        userEntity.setVerifiedUser(true);
        userEntityRepository.save(userEntity);
        verificationToken.setUsedAt(LocalDateTime.now());
        verificationTokenRepository.save(verificationToken);
        VerifyOtpResponse verifyOtpResponse = buildOTPVerificationResponseInstance(userEntity);
        verifyOtpResponse.setOtpVerificationMessage(VERIFICATION_OTP_SUCCESS_MESSAGE.getMessage());
        return verifyOtpResponse;
    }

    @Override
    public VerifyOtpResponse resendOtp(ResendOtpRequest resendOtpRequest) {
        UserEntity userEntity = userEntityRepository.findByEmail(resendOtpRequest.getEmail()).
                orElseThrow(()-> new UserNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        verificationTokenRepository.findByUserEntityAndUsedAtIsNull(userEntity)
                .ifPresent(oldToken -> {oldToken.setUsedAt(LocalDateTime.now());
                    verificationTokenRepository.save(oldToken);});
        String  newOtp = OtpGenerator.generateOtp(6);
        String hashNewOtp = passwordEncoder.encode(newOtp);
        VerificationToken verificationToken = buildVerificationTokenInstance(hashNewOtp, userEntity);
        verificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(userEntity.getEmail(), newOtp);
        VerifyOtpResponse verifyOtpResponse = new VerifyOtpResponse();
        verifyOtpResponse.setEmail(userEntity.getEmail());
        verifyOtpResponse.setVerifiedAt(verificationToken.getExpiredAt().toString());
        verifyOtpResponse.setOtpVerificationMessage(VERIFICATION_OTP_SUCCESS_MESSAGE.getMessage());
        return verifyOtpResponse;
    }

    private static @NotNull VerificationToken buildVerificationTokenInstance(String hashNewOtp, UserEntity userEntity) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(hashNewOtp);
        verificationToken.setUserEntity(userEntity);
        verificationToken.setExpiredAt(LocalDateTime.now());
        return verificationToken;
    }

    private static @NotNull VerifyOtpResponse buildOTPVerificationResponseInstance(UserEntity userEntity) {
        VerifyOtpResponse verifyOtpResponse = new VerifyOtpResponse();
        verifyOtpResponse.setEmail(userEntity.getEmail());
        verifyOtpResponse.setVerifiedAt(LocalDateTime.now().toString());
        return verifyOtpResponse;
    }
}
