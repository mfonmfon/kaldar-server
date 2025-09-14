package com.kaldar.kaldar.kaldarService.implimentation;
import com.kaldar.kaldar.contants.OrderStatus;
import com.kaldar.kaldar.contants.Role;
import com.kaldar.kaldar.domain.entities.*;
import com.kaldar.kaldar.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.domain.repository.OrderEntityRepository;
import com.kaldar.kaldar.domain.repository.VerificationTokenRepository;
import com.kaldar.kaldar.dtos.request.AcceptOrderRequest;
import com.kaldar.kaldar.dtos.request.AcceptOrderResponse;
import com.kaldar.kaldar.dtos.request.DryCleanerRegistrationRequest;
import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.exceptions.*;
import com.kaldar.kaldar.kaldarService.interfaces.DryCleanerService;
import com.kaldar.kaldar.kaldarService.interfaces.EmailService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.kaldar.kaldar.contants.StatusResponse.*;

@Service
public class DefaultDryCleanerService implements DryCleanerService {
    private final DryCleanerEntityRepository dryCleanerEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final int otpDigits;
    private final int expiredOtpMin;
    private final CustomerEntityRepository customerEntityRepository;
    private final OrderEntityRepository orderEntityRepository;


    public DefaultDryCleanerService(DryCleanerEntityRepository dryCleanerEntityRepository, PasswordEncoder passwordEncoder,
                                    VerificationTokenRepository verificationTokenRepository, EmailService emailService,
                                    @Value("${security.otp.digit}")int otpDigits,
                                    @Value("${security.otp.expiry-minutes}")int expiredOtpMin, CustomerEntityRepository customerEntityRepository,
                                    OrderEntityRepository orderEntityRepository) {
        this.dryCleanerEntityRepository = dryCleanerEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.otpDigits = otpDigits;
        this.expiredOtpMin = expiredOtpMin;
        this.customerEntityRepository = customerEntityRepository;
        this.orderEntityRepository = orderEntityRepository;

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
        VerificationToken verificationToken = buildVerificationToken(hashPlainOtpDigits, expiredAt, dryCleanerEntity);
        verificationTokenRepository.save(verificationToken);
        SendVerificationEmailResponse sendVerificationEmailResponse =
                emailService.sendVerificationEmail(dryCleanerEntity.getEmail(), otpDigitNumberGenerator);
        sendVerificationEmailResponse.setEmail(dryCleanerEntity.getEmail());
        sendVerificationEmailResponse.setExpiresAt(expiredAt.toString());
        sendVerificationEmailResponse.setVerificationMessage(VERIFICATION_TOKEN_SENT_MESSAGE.getMessage());
        return sendVerificationEmailResponse;
    }

    private static @NotNull VerificationToken buildVerificationToken(String hashPlainOtpDigits, LocalDateTime expiredAt,
                                                                     DryCleanerEntity dryCleanerEntity) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(hashPlainOtpDigits);
        verificationToken.setExpiredAt(expiredAt);
        verificationToken.setUserEntity(dryCleanerEntity);
        return verificationToken;
    }

    @Override
    public AcceptOrderResponse acceptOrder(AcceptOrderRequest acceptOrderRequest) {
        OrderEntity orders = orderEntityRepository.findById(acceptOrderRequest.getOrderId())
                .orElseThrow(()-> new OrdersNotFoundException(ORDERS_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        DryCleanerEntity dryCleanerEntity = dryCleanerEntityRepository.findById(acceptOrderRequest.getDryCleanerId())
                .orElseThrow(()-> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        //check if the order is assign to the drycleaner
        if (orders.getDryCleaner() == null || !orders.getDryCleaner().getId().equals(dryCleanerEntity.getId())){
            throw new InvalidOrderAssignmentException("Order is not assign to this drycleaner");
        }
        if (orders.getOrderStatus() != OrderStatus.ACCEPTED && orders.getOrderStatus() != OrderStatus.PENDING_ACCEPTANCE){
            throw new InvalidOrderAssignmentException("Order cannot be accepted ");
        }
        if (Boolean.TRUE.equals(dryCleanerEntity.isActive())){
            throw new NoActiveDryCleanerExcpetion("Drycleaner is not active");
        }
        orders.setDryCleaner(dryCleanerEntity);
        orders.setOrderStatus(OrderStatus.SCHEDULED);
        if (orders.getPickupAt() == null){
            orders.setPickupAt(LocalDateTime.now().plusHours(24));
        }
        List<String> missingService = findMissingService(orders, dryCleanerEntity);
        if (missingService.isEmpty())
            throw new MissingServiceException("Can not accept order; missing service");

        orderEntityRepository.save(orders);
        AcceptOrderResponse acceptOrderResponse = new AcceptOrderResponse();
        acceptOrderResponse.setStatus("SUCCESS");
        return acceptOrderResponse;
    }

    private List<String> findMissingService(OrderEntity orders, DryCleanerEntity dryCleanerEntity) {
        Set<String> availableService = dryCleanerEntity.getServiceOfferings() == null ? Collections.emptySet()
                : dryCleanerEntity.getServiceOfferings().stream().map(ServiceOfferings::getName).collect(Collectors.toSet());
        return orders.getOrderLines() == null ? Collections.emptyList()
                : orders.getOrderLines().stream().map(OrderLine::getClothType)
                .filter(line -> !availableService.contains(line))
                .distinct()
                .collect(Collectors.toList());
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
