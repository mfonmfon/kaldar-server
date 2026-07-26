package com.kaldar.kaldar.drycleaner.application.service.impl;

import com.kaldar.kaldar.shared.domain.constants.Role;
import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import com.kaldar.kaldar.shared.infrastructure.auth.domain.model.VerificationToken;
import com.kaldar.kaldar.customer.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.drycleaner.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.order.domain.repository.OrderEntityRepository;
import com.kaldar.kaldar.shared.infrastructure.auth.domain.repository.VerificationTokenRepository;
import com.kaldar.kaldar.drycleaner.application.dto.request.*;
import com.kaldar.kaldar.drycleaner.application.dto.response.DryCleanerProfileResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.shared.domain.exceptions.*;
import com.kaldar.kaldar.drycleaner.application.service.DryCleanerService;
import com.kaldar.kaldar.shared.infrastructure.email.service.EmailService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.*;

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
    private final com.kaldar.kaldar.drycleaner.domain.repository.ServiceOfferingRepository serviceOfferingRepository;
    private final com.kaldar.kaldar.drycleaner.domain.repository.BusinessVerificationRepository businessVerificationRepository;
    private final com.kaldar.kaldar.order.domain.repository.ReviewRepository reviewRepository;
    private final com.kaldar.kaldar.wallet.domain.repository.WalletRepository walletRepository;

    public DefaultDryCleanerService(DryCleanerEntityRepository dryCleanerEntityRepository,
            PasswordEncoder passwordEncoder,
            VerificationTokenRepository verificationTokenRepository, EmailService emailService,
            @Value("${security.otp.digit}") int otpDigits,
            @Value("${security.otp.expiry-minutes}") int expiredOtpMin,
            CustomerEntityRepository customerEntityRepository,
            com.kaldar.kaldar.order.domain.repository.OrderEntityRepository orderEntityRepository,
            com.kaldar.kaldar.drycleaner.domain.repository.ServiceOfferingRepository serviceOfferingRepository,
            com.kaldar.kaldar.drycleaner.domain.repository.BusinessVerificationRepository businessVerificationRepository,
            com.kaldar.kaldar.order.domain.repository.ReviewRepository reviewRepository,
            com.kaldar.kaldar.wallet.domain.repository.WalletRepository walletRepository) {
        this.dryCleanerEntityRepository = dryCleanerEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.otpDigits = otpDigits;
        this.expiredOtpMin = expiredOtpMin;
        this.customerEntityRepository = customerEntityRepository;
        this.orderEntityRepository = orderEntityRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.businessVerificationRepository = businessVerificationRepository;
        this.reviewRepository = reviewRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public java.util.List<com.kaldar.kaldar.drycleaner.application.dto.response.ServiceOfferingResponse> getServices(
            Long dryCleanerId) {
        return serviceOfferingRepository.findByDryCleanerId(dryCleanerId).stream()
                .map(this::mapToServiceOfferingResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public com.kaldar.kaldar.drycleaner.application.dto.response.ServiceOfferingResponse addOrUpdateService(
            Long dryCleanerId, com.kaldar.kaldar.drycleaner.application.dto.request.ServiceOfferingRequest request) {
        DryCleanerEntity dryCleaner = dryCleanerEntityRepository.findById(dryCleanerId)
                .orElseThrow(() -> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));

        com.kaldar.kaldar.drycleaner.domain.model.ServiceOffering offering = serviceOfferingRepository
                .findByDryCleanerIdAndServiceName(dryCleanerId, request.getServiceName())
                .orElse(new com.kaldar.kaldar.drycleaner.domain.model.ServiceOffering());

        offering.setDryCleaner(dryCleaner);
        offering.setServiceName(request.getServiceName());
        offering.setUnitPrice(request.getUnitPrice());
        offering.setDescription(request.getDescription());
        offering.setCreatedAt(LocalDateTime.now());
        offering.setUpdatedAt(LocalDateTime.now());

        com.kaldar.kaldar.drycleaner.domain.model.ServiceOffering saved = serviceOfferingRepository.save(offering);
        return mapToServiceOfferingResponse(saved);
    }

    private com.kaldar.kaldar.drycleaner.application.dto.response.ServiceOfferingResponse mapToServiceOfferingResponse(
            com.kaldar.kaldar.drycleaner.domain.model.ServiceOffering offering) {
        com.kaldar.kaldar.drycleaner.application.dto.response.ServiceOfferingResponse resp = new com.kaldar.kaldar.drycleaner.application.dto.response.ServiceOfferingResponse();
        resp.setId(offering.getId());
        resp.setServiceName(offering.getServiceName());
        resp.setUnitPrice(offering.getUnitPrice());
        resp.setDescription(offering.getDescription());
        return resp;
    }

    @Override
    public SendVerificationEmailResponse registerDryCleaner(
            DryCleanerRegistrationRequest dryCleanerRegistrationRequest) {
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
        SendVerificationEmailResponse sendVerificationEmailResponse = emailService
                .sendVerificationEmail(dryCleanerEntity.getEmail(), otpDigitNumberGenerator);
        sendVerificationEmailResponse.setEmail(dryCleanerEntity.getEmail());
        sendVerificationEmailResponse.setExpiresAt(expiredAt.toString());
        sendVerificationEmailResponse.setVerificationMessage(VERIFICATION_TOKEN_SENT_MESSAGE.getMessage());
        return sendVerificationEmailResponse;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public DryCleanerProfileResponse editProfile(UpdateDryCleanerProfileRequest updateDryCleanerProfileRequest) {
        DryCleanerEntity dryCleanerEntity = dryCleanerEntityRepository
                .findById(updateDryCleanerProfileRequest.getDryCleanerId())
                .orElseThrow(() -> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        mapDyrCleanerProfileUpdateRequest(updateDryCleanerProfileRequest, dryCleanerEntity);
        DryCleanerProfileResponse dryCleanerProfileResponse = new DryCleanerProfileResponse();
        dryCleanerProfileResponse.setMessage(DRY_CLEANER_PROFILE_UPDATED_SUCCESS_MESSAGE.getMessage());
        return dryCleanerProfileResponse;
    }

    private static void mapDyrCleanerProfileUpdateRequest(UpdateDryCleanerProfileRequest updateDryCleanerProfileRequest,
            DryCleanerEntity dryCleanerEntity) {
        dryCleanerEntity.setFirstName(updateDryCleanerProfileRequest.getFirstName());
        dryCleanerEntity.setLastName(updateDryCleanerProfileRequest.getLastName());
        dryCleanerEntity.setBusinessName(updateDryCleanerProfileRequest.getBusinessName());
        dryCleanerEntity.setBusinessAddress(updateDryCleanerProfileRequest.getShopAddress());
        dryCleanerEntity.setPhoneNumber(updateDryCleanerProfileRequest.getBusinessPhoneNumber());
        dryCleanerEntity.setUpdatedAt(updateDryCleanerProfileRequest.getUpdatedAt());
    }

    private static @NotNull VerificationToken buildVerificationToken(String hashPlainOtpDigits, LocalDateTime expiredAt,
            DryCleanerEntity dryCleanerEntity) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(hashPlainOtpDigits);
        verificationToken.setExpiredAt(expiredAt);
        verificationToken.setUserEntity(dryCleanerEntity);
        return verificationToken;
    }

    private String generateOtp(int otpDigits) {
        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int index = 0; index < otpDigits; index++) {
            stringBuilder.append(secureRandom.nextInt(10));
        }
        return stringBuilder.toString();
    }

    private static DryCleanerEntity buildDryCleanerEntityInstance(
            DryCleanerRegistrationRequest dryCleanerRegistrationRequest) {
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
        if (isDryCleanerBusinessEmailExist)
            throw new DryCleanerBusinessEmailExistException("Business email already exists");
    }

    private void validateDryCleanerEmailExist(String email) {
        boolean isEmailExist = dryCleanerEntityRepository.existsByEmail(email);
        if (isEmailExist)
            throw new DryCleanerEmailAlreadyExistException("DryCleaner with this email already exists");
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void updatePayoutAccount(Long dryCleanerId, String accountName, String accountNumber, String bankCode,
            String bankName) {
        DryCleanerEntity dryCleaner = dryCleanerEntityRepository.findById(dryCleanerId)
                .orElseThrow(() -> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        dryCleaner.setAccountName(accountName);
        dryCleaner.setAccountNumber(accountNumber);
        dryCleaner.setBankCode(bankCode);
        dryCleaner.setBankName(bankName);
        dryCleaner.setUpdatedAt(LocalDateTime.now());
        dryCleanerEntityRepository.save(dryCleaner);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void updateWorkingHours(Long dryCleanerId, String workingHoursJson) {
        DryCleanerEntity dryCleaner = dryCleanerEntityRepository.findById(dryCleanerId)
                .orElseThrow(() -> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        dryCleaner.setWorkingHours(workingHoursJson);
        dryCleaner.setUpdatedAt(LocalDateTime.now());
        dryCleanerEntityRepository.save(dryCleaner);
    }

    @Override
    public com.kaldar.kaldar.drycleaner.application.dto.response.OnboardingStatusResponse getOnboardingStatus(
            Long dryCleanerId) {
        DryCleanerEntity dryCleaner = dryCleanerEntityRepository.findById(dryCleanerId)
                .orElseThrow(() -> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));

        com.kaldar.kaldar.drycleaner.application.dto.response.OnboardingStatusResponse status = new com.kaldar.kaldar.drycleaner.application.dto.response.OnboardingStatusResponse();

        status.setBusinessVerified(businessVerificationRepository.findByDryCleanerId(dryCleanerId).isPresent());
        status.setStoreProfileSetup(dryCleaner.getBusinessName() != null && !dryCleaner.getBusinessName().isEmpty());
        status.setPayoutAccountAdded(dryCleaner.getAccountNumber() != null);
        status.setBusinessOperationsSetup(dryCleaner.getWorkingHours() != null);
        status.setStoreInventorySetup(
                dryCleaner.getServiceOfferings() != null && !dryCleaner.getServiceOfferings().isEmpty());

        return status;
    }

    @Override
    public com.kaldar.kaldar.drycleaner.application.dto.response.AnalyticsResponse getAnalytics(Long dryCleanerId,
            String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = switch (period.toLowerCase()) {
            case "today" -> now.withHour(0).withMinute(0).withSecond(0).withNano(0);
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            default -> now.minusYears(100);
        };

        // Fetch all orders for the dry cleaner and filter by period
        java.util.List<com.kaldar.kaldar.order.domain.model.OrderEntity> orders = orderEntityRepository
                .findByDryCleanerIdOrderByCreatedAtDesc(dryCleanerId).stream()
                .filter(o -> o.getCreatedAt() != null
                        && (o.getCreatedAt().isAfter(startDate) || o.getCreatedAt().isEqual(startDate)))
                .collect(java.util.stream.Collectors.toList());

        // Calculate revenue
        java.math.BigDecimal revenue = orders.stream()
                .map(order -> java.math.BigDecimal
                        .valueOf(order.getTotalAmount() != null ? order.getTotalAmount() : 0.0))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        // Calculate average rating
        Double avgRating = reviewRepository.findByDryCleanerIdOrderByCreatedAtDesc(dryCleanerId).stream()
                .mapToDouble(com.kaldar.kaldar.order.domain.model.ReviewEntity::getRating)
                .average()
                .orElse(0.0);

        return new com.kaldar.kaldar.drycleaner.application.dto.response.AnalyticsResponse(
                revenue,
                "+12.5%", // Mock change
                (long) orders.size(),
                "+5", // Mock change
                avgRating,
                "+0.2" // Mock change
        );
    }

    @Override
    public com.kaldar.kaldar.drycleaner.application.dto.response.AnalyticsResponse findDrycleanerByBusinessName(
            String businessName) {
        // Find by business name and return analytics (as defined by interface type)
        java.util.List<DryCleanerEntity> list = dryCleanerEntityRepository.findAll();
        for (DryCleanerEntity d : list) {
            if (businessName.equalsIgnoreCase(d.getBusinessName())) {
                return getAnalytics(d.getId(), "month");
            }
        }
        throw new UserNotFoundException("Drycleaner with name " + businessName + " not found");
    }

    @Override
    public com.kaldar.kaldar.drycleaner.application.dto.response.DryCleanerProfileResponse getProfile() {
        // Return dummy / authenticated dry cleaner profile response
        com.kaldar.kaldar.drycleaner.application.dto.response.DryCleanerProfileResponse resp = new com.kaldar.kaldar.drycleaner.application.dto.response.DryCleanerProfileResponse();
        resp.setMessage("Profile fetched successfully");
        return resp;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteDryCleaner(Long dryCleanerId) {
        DryCleanerEntity dryCleaner = dryCleanerEntityRepository.findById(dryCleanerId)
                .orElseThrow(() -> new UserNotFoundException("Dry cleaner not found with ID: " + dryCleanerId));
        walletRepository.findByUserId(dryCleanerId).ifPresent(walletRepository::delete);
        dryCleanerEntityRepository.delete(dryCleaner);
    }
}
