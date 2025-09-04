package com.kaldar.kaldar.kaldarService;

import com.kaldar.kaldar.contants.Role;
import com.kaldar.kaldar.domain.entities.DryCleanerEntity;
import com.kaldar.kaldar.domain.entities.VerificationToken;
import com.kaldar.kaldar.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.domain.repository.VerificationTokenRepository;
import com.kaldar.kaldar.dtos.request.DryCleanerRegistrationRequest;
import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.exceptions.DryCleanerBusinessEmailExistException;
import com.kaldar.kaldar.exceptions.DryCleanerEmailAlreadyExistException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import static com.kaldar.kaldar.contants.StatusResponse.VERIFICATION_TOKEN_SENT_MESSAGE;

@Service
public class DefaultDryCleanerService implements DryCleanerService{
    private final DryCleanerEntityRepository dryCleanerEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final int otpDigits;
    private final int expiredOtpMin;

    public DefaultDryCleanerService(DryCleanerEntityRepository dryCleanerEntityRepository, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository, EmailService emailService,
                                    @Value("${security.otp.digit}")int otpDigits,
                                    @Value("${security.otp.expiry-minutes}")int expiredOtpMin) {
        this.dryCleanerEntityRepository = dryCleanerEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.otpDigits = otpDigits;
        this.expiredOtpMin = expiredOtpMin;
    }

    @Override
    public SendVerificationEmailResponse registerDryCleaner(DryCleanerRegistrationRequest dryCleanerRegistrationRequest) {
        validateDryCleanerEmailExist(dryCleanerRegistrationRequest.getEmail());
        validateDryCleanerBusinessEmailExist(dryCleanerRegistrationRequest.getBusinessEmail());
        DryCleanerEntity dryCleanerEntity = buildDryCleanerEntityInstance(dryCleanerRegistrationRequest);
        dryCleanerEntity.setPassword(passwordEncoder.encode(dryCleanerRegistrationRequest.getPassword()));
        dryCleanerEntity.setVerifiedUser(false);
        dryCleanerEntityRepository.save(dryCleanerEntity);

        String otpDigitNumberGenerator = generateOtp(otpDigits);
        String hashPlainOtpDigits = passwordEncoder.encode(otpDigitNumberGenerator);
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(expiredOtpMin);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(hashPlainOtpDigits);
        verificationToken.setExpiredAt(expiredAt);
        verificationToken.setUserEntity(dryCleanerEntity);

        verificationTokenRepository.save(verificationToken);
        SendVerificationEmailResponse sendVerificationEmailResponse =
                emailService.sendVerificationEmail(dryCleanerEntity.getEmail(), otpDigitNumberGenerator);
        sendVerificationEmailResponse.setEmail(dryCleanerEntity.getEmail());
        sendVerificationEmailResponse.setExpiresAt(expiredAt.toString());
        sendVerificationEmailResponse.setVerificationMessage(VERIFICATION_TOKEN_SENT_MESSAGE.getMessage());
        return sendVerificationEmailResponse;
    }

    private String generateOtp(int otpDigits) {
        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int index = 0; index < otpDigits; index++){
            stringBuilder.append(secureRandom.nextInt(10));
        }
        return stringBuilder.toString();
    }

    private static DryCleanerEntity buildDryCleanerEntityInstance(DryCleanerRegistrationRequest dryCleanerRegistrationRequest) {
        DryCleanerEntity dryCleanerEntity = new DryCleanerEntity();
        dryCleanerEntity.setFirstName(dryCleanerRegistrationRequest.getFirstName());
        dryCleanerEntity.setLastName(dryCleanerRegistrationRequest.getLastName());
        dryCleanerEntity.setEmail(dryCleanerRegistrationRequest.getEmail());
        dryCleanerEntity.setBusinessName(dryCleanerRegistrationRequest.getBusinessName());
        dryCleanerEntity.setBusinessAddress(dryCleanerRegistrationRequest.getShopAddress());
        dryCleanerEntity.setPhoneNumber(dryCleanerRegistrationRequest.getBusinessPhoneNumber());
        dryCleanerEntity.getRoles().add(Role.DRY_CLEANER);
        return dryCleanerEntity;
    }

    private void validateDryCleanerBusinessEmailExist(String businessEmail) {
        boolean isDryCleanerBusinessEmailExist = dryCleanerEntityRepository.existsByEmail(businessEmail);
        if (isDryCleanerBusinessEmailExist)throw new DryCleanerBusinessEmailExistException("Business email already exists");
    }
    private void validateDryCleanerEmailExist(String email) {
        boolean isEmailExist = dryCleanerEntityRepository.existsByEmail(email);
        if (isEmailExist)throw new DryCleanerEmailAlreadyExistException("DryCleaner with this email already exists");

    }
}
