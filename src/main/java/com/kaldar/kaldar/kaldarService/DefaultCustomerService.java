package com.kaldar.kaldar.kaldarService;
import com.kaldar.kaldar.contants.Role;
import com.kaldar.kaldar.domain.entities.CustomerEntity;
import com.kaldar.kaldar.domain.entities.VerificationToken;
import com.kaldar.kaldar.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.domain.repository.VerificationTokenRepository;
import com.kaldar.kaldar.dtos.request.CustomerRegistrationRequest;
import com.kaldar.kaldar.dtos.response.CustomerRegistrationResponse;
import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.exceptions.CustomerEmailAlreadyExist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import static com.kaldar.kaldar.contants.StatusResponse.CUSTOMER_REGISTRATION_SUCCESS_MESSAGE;
import static com.kaldar.kaldar.contants.StatusResponse.VERIFICATION_TOKEN_SENT_MESSAGE;


@Service
public class DefaultCustomerService implements CustomerService{
    private final CustomerEntityRepository customerEntityRepository;
    private final JwtService jwtService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    //Otp Configuration
    private final int otpDigits;
    private final int otpExpiryMinutes;
//    private static final  Pattern EMAIL_VALIDATION_REGEX

    public DefaultCustomerService(CustomerEntityRepository customerEntityRepository, JwtService jwtService,
                                  VerificationTokenRepository verificationTokenRepository, EmailService emailService,
                                  PasswordEncoder passwordEncoder,
                                  @Value("${security.otp.digit:6}") int otpDigits,
                                  @Value("${security.otp.expiry-minutes:15}") int otpExpiryMinutes) {
        this.customerEntityRepository = customerEntityRepository;
        this.jwtService = jwtService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.otpDigits = otpDigits;
        this.otpExpiryMinutes = otpExpiryMinutes;
    }

    @Override
    public CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        validateCustomerEmailExist(customerRegistrationRequest.getEmail());
        CustomerEntity customerEntity = buildCustomerEntityInstance(customerRegistrationRequest);
        CustomerEntity savedCustomer =  customerEntityRepository.save(customerEntity);

        String otpDigitsNumbersGenerate = generateOtp(otpDigits);
        String hashedOtpDigits = passwordEncoder.encode(otpDigitsNumbersGenerate);
        VerificationToken verificationToken = new VerificationToken();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(otpExpiryMinutes);
        verificationToken.setToken(hashedOtpDigits);
        verificationToken.setUserEntity(customerEntity);
        verificationToken.setExpiredAt(expiredAt);
        verificationTokenRepository.save(verificationToken);

        SendVerificationEmailResponse sendVerificationEmailResponse = emailService.sendVerificationEmail(customerEntity.getEmail(), otpDigitsNumbersGenerate);
        sendVerificationEmailResponse.setEmail(customerEntity.getEmail());
        sendVerificationEmailResponse.setExpiresAt(expiredAt.toString());
        sendVerificationEmailResponse.setVerificationMessage(VERIFICATION_TOKEN_SENT_MESSAGE.getMessage());

        CustomerRegistrationResponse customerRegistrationResponse = new CustomerRegistrationResponse();
        customerRegistrationResponse.setMessage(CUSTOMER_REGISTRATION_SUCCESS_MESSAGE.getMessage());
        return customerRegistrationResponse;
    }

    private CustomerEntity buildCustomerEntityInstance(CustomerRegistrationRequest customerRegistrationRequest) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(customerRegistrationRequest.getFirstName());
        customerEntity.setLastName(customerRegistrationRequest.getLastName());
        customerEntity.setEmail(customerRegistrationRequest.getEmail());
        customerEntity.setPhoneNumber(customerRegistrationRequest.getPhoneNumber());
        customerEntity.setDefaultAddress(customerRegistrationRequest.getAddress());
        customerEntity.setPassword((customerRegistrationRequest.getPassword()));
        customerEntity.getRoles().add(Role.CUSTOMER);
        customerEntity.setPassword(passwordEncoder.encode(customerRegistrationRequest.getPassword()));
        return customerEntity;
    }

    private String generateOtp(int digits) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(digits);
        for (int index = 0; index < digits; index++){
         stringBuilder.append(secureRandom.nextInt(10));
        }
        return stringBuilder.toString();
    }


    private void validateCustomerEmailExist(String email) {
        Boolean isCustomerEmailExist = customerEntityRepository.existsByEmail(email);
        if (isCustomerEmailExist)throw new CustomerEmailAlreadyExist("Email Already Exist");
    }
}