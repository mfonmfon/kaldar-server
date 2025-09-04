package com.kaldar.kaldar.kaldarService;

import com.kaldar.kaldar.domain.entities.CustomerEntity;
import com.kaldar.kaldar.domain.entities.UserEntity;
import com.kaldar.kaldar.domain.entities.VerificationToken;
import com.kaldar.kaldar.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.domain.repository.UserEntityRepository;
import com.kaldar.kaldar.domain.repository.VerificationTokenRepository;
import com.kaldar.kaldar.dtos.request.VerifyOtpRequest;
import com.kaldar.kaldar.dtos.response.VerifyOtpResponse;
import com.kaldar.kaldar.exceptions.CustomerNotFoundException;
import com.kaldar.kaldar.exceptions.ExpiredOtpException;
import com.kaldar.kaldar.exceptions.InvalidOtpException;
import com.kaldar.kaldar.exceptions.OTPNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.kaldar.kaldar.contants.StatusResponse.*;

@Service
public class DefaultVerificationTokenService implements VerificationTokenService{
    private final CustomerEntityRepository customerEntityRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityRepository userEntityRepository;

    public DefaultVerificationTokenService(CustomerEntityRepository customerEntityRepository, VerificationTokenRepository verificationTokenRepository, PasswordEncoder passwordEncoder, UserEntityRepository userEntityRepository) {
        this.customerEntityRepository = customerEntityRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest verifyOtpRequest) {
        UserEntity userEntity = userEntityRepository.findByEmail(verifyOtpRequest.getEmail())
                .orElseThrow(()-> new CustomerNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));

        VerificationToken verificationToken = verificationTokenRepository.findByUserEntityAndUsedAtIsNull(userEntity)
                .orElseThrow(()-> new OTPNotFoundException(OTP_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));

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

    private static @NotNull VerifyOtpResponse buildOTPVerificationResponseInstance(UserEntity userEntity) {
        VerifyOtpResponse verifyOtpResponse = new VerifyOtpResponse();
        verifyOtpResponse.setEmail(userEntity.getEmail());
        verifyOtpResponse.setVerifiedAt(LocalDateTime.now().toString());
        return verifyOtpResponse;
    }
}
