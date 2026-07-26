package com.kaldar.kaldar.customer.application.service.impl;

import com.kaldar.kaldar.customer.application.dto.request.ChangePasswordRequest;
import com.kaldar.kaldar.customer.application.dto.request.CustomerRegistrationRequest;
import com.kaldar.kaldar.customer.application.dto.request.UpdateCustomerProfileRequest;
import com.kaldar.kaldar.customer.application.dto.response.ChangePasswordResponse;
import com.kaldar.kaldar.customer.application.dto.response.CustomerProfileResponse;
import com.kaldar.kaldar.customer.application.dto.response.CustomerRegistrationResponse;
import com.kaldar.kaldar.customer.application.service.CustomerService;
import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.customer.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.shared.domain.constants.Role;
import com.kaldar.kaldar.shared.domain.exceptions.CustomerEmailAlreadyExist;
import com.kaldar.kaldar.shared.domain.exceptions.EmptyRequiredFieldException;
import com.kaldar.kaldar.shared.domain.exceptions.PasswordMismatchException;
import com.kaldar.kaldar.shared.domain.exceptions.UserNotFoundException;
import com.kaldar.kaldar.shared.infrastructure.auth.domain.model.VerificationToken;
import com.kaldar.kaldar.shared.infrastructure.auth.domain.repository.VerificationTokenRepository;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.service.impl.JwtService;
import com.kaldar.kaldar.shared.infrastructure.email.service.EmailService;
import com.kaldar.kaldar.shared.infrastructure.utility.OtpGenerator;
import com.kaldar.kaldar.wallet.domain.repository.WalletRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.*;

@Service
public class DefaultCustomerService implements CustomerService {
    private final CustomerEntityRepository customerEntityRepository;
    private final JwtService jwtService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;
    private final int otpDigits;
    private final int otpExpiryMinutes;

    public DefaultCustomerService(CustomerEntityRepository customerEntityRepository, JwtService jwtService,
            VerificationTokenRepository verificationTokenRepository, EmailService emailService,
            PasswordEncoder passwordEncoder, WalletRepository walletRepository,
            @Value("${security.otp.digit:6}") int otpDigits,
            @Value("${security.otp.expiry-minutes:15}") int otpExpiryMinutes) {
        this.customerEntityRepository = customerEntityRepository;
        this.jwtService = jwtService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.walletRepository = walletRepository;
        this.otpDigits = otpDigits;
        this.otpExpiryMinutes = otpExpiryMinutes;
    }

    @Override
    public CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        validateCustomerEmailExist(customerRegistrationRequest.getEmail());
        CustomerEntity customerEntity = buildCustomerEntityInstance(customerRegistrationRequest);
        customerEntity.setPassword(passwordEncoder.encode(customerRegistrationRequest.getPassword()));
        CustomerEntity savedCustomer = customerEntityRepository.save(customerEntity);
        String otpDigitsNumbersGenerate = OtpGenerator.generateOtp(otpDigits);
        String hashedOtpDigits = passwordEncoder.encode(otpDigitsNumbersGenerate);
        VerificationToken verificationToken = new VerificationToken();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(otpExpiryMinutes);
        verificationToken.setToken(hashedOtpDigits);
        verificationToken.setUserEntity(customerEntity);
        verificationToken.setExpiredAt(expiredAt);
        verificationTokenRepository.save(verificationToken);
        SendVerificationEmailResponse sendVerificationEmailResponse = emailService
                .sendVerificationEmail(customerEntity.getEmail(), otpDigitsNumbersGenerate);
        sendVerificationEmailResponse.setEmail(customerEntity.getEmail());
        sendVerificationEmailResponse.setExpiresAt(expiredAt.toString());
        sendVerificationEmailResponse.setVerificationMessage(VERIFICATION_TOKEN_SENT_MESSAGE.getMessage());
        CustomerRegistrationResponse customerRegistrationResponse = new CustomerRegistrationResponse();
        customerRegistrationResponse.setMessage(CUSTOMER_REGISTRATION_SUCCESS_MESSAGE.getMessage());
        return customerRegistrationResponse;
    }

    @Override
    public CustomerProfileResponse updateCustomerProfile(UpdateCustomerProfileRequest updateCustomerProfileRequest) {
        CustomerEntity customerEntity = customerEntityRepository.findById(updateCustomerProfileRequest.getCustomerId())
                .orElseThrow(() -> new UserNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        customerEntity.setFirstName(updateCustomerProfileRequest.getFirstName());
        customerEntity.setLastName(updateCustomerProfileRequest.getLastName());
        customerEntity.setDefaultAddress(updateCustomerProfileRequest.getDefaultAddress());
        customerEntity.setPhoneNumber(updateCustomerProfileRequest.getPhoneNumber());
        customerEntityRepository.save(customerEntity);
        return buildCustomerProfileResponse(customerEntity);
    }

    @Override
    public CustomerProfileResponse getCustomerProfile(Long customerId) {
        CustomerEntity customerEntity = customerEntityRepository.findById(customerId)
                .orElseThrow(() -> new UserNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        return buildCustomerProfileResponse(customerEntity);
    }

    @Override
    public ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        CustomerEntity customerEntity = customerEntityRepository.findById(changePasswordRequest.getCustomerId())
                .orElseThrow(() -> new UserNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        validateOldPasswordMatch(changePasswordRequest, customerEntity);
        validatePasswordNotBlank(changePasswordRequest);
        customerEntity.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        customerEntityRepository.save(customerEntity);
        ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse();
        changePasswordResponse.setStatusCode("SUCCESS");
        return changePasswordResponse;
    }

    @Override
    @Transactional
    public void deleteCustomer(Long customerId) {
        CustomerEntity customerEntity = customerEntityRepository.findById(customerId)
                .orElseThrow(() -> new UserNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        walletRepository.findByUserId(customerId).ifPresent(walletRepository::delete);
        verificationTokenRepository.findByUserEntityAndUsedAtIsNull(customerEntity).ifPresent(verificationTokenRepository::delete);
        customerEntityRepository.delete(customerEntity);
    }

    private static void validatePasswordNotBlank(ChangePasswordRequest changePasswordRequest) {
        if (changePasswordRequest.getNewPassword() == null || changePasswordRequest.getNewPassword().isBlank()) {
            throw new EmptyRequiredFieldException("Password can not be empty");
        }
    }

    private void validateOldPasswordMatch(ChangePasswordRequest changePasswordRequest, CustomerEntity customerEntity) {
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), customerEntity.getPassword())) {
            throw new PasswordMismatchException("Password does not match");
        }
    }

    private static @NotNull CustomerProfileResponse buildCustomerProfileResponse(CustomerEntity customerEntity) {
        CustomerProfileResponse customerProfileResponse = new CustomerProfileResponse();
        customerProfileResponse.setFirstName(customerEntity.getFirstName());
        customerProfileResponse.setLastName(customerEntity.getLastName());
        customerProfileResponse.setPhoneNumber(customerEntity.getPhoneNumber());
        customerProfileResponse.setAddress(customerEntity.getDefaultAddress());
        customerProfileResponse.setMessage(CUSTOMER_PROFILE_UPDATE_STATUS_MESSAGE.getMessage());
        return customerProfileResponse;
    }

    private CustomerEntity buildCustomerEntityInstance(CustomerRegistrationRequest customerRegistrationRequest) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(customerRegistrationRequest.getFirstName());
        customerEntity.setLastName(customerRegistrationRequest.getLastName());
        customerEntity.setEmail(customerRegistrationRequest.getEmail());
        customerEntity.setPhoneNumber(customerRegistrationRequest.getPhoneNumber());
        customerEntity.setDefaultAddress(customerRegistrationRequest.getAddress());
        customerEntity.getRoles().add(Role.CUSTOMER);
        return customerEntity;
    }

    private void validateCustomerEmailExist(String email) {
        Boolean isCustomerEmailExist = customerEntityRepository.existsByEmail(email);
        if (isCustomerEmailExist)
            throw new CustomerEmailAlreadyExist("Email Already Exist");
    }
}
