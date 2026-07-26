package com.kaldar.kaldar.drycleaner.service;

import com.kaldar.kaldar.drycleaner.application.dto.request.*;
import com.kaldar.kaldar.drycleaner.application.dto.response.*;
import com.kaldar.kaldar.drycleaner.application.service.impl.DefaultDryCleanerService;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import com.kaldar.kaldar.drycleaner.domain.model.ServiceOffering;
import com.kaldar.kaldar.drycleaner.domain.repository.BusinessVerificationRepository;
import com.kaldar.kaldar.drycleaner.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.drycleaner.domain.repository.ServiceOfferingRepository;
import com.kaldar.kaldar.customer.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.order.domain.repository.OrderEntityRepository;
import com.kaldar.kaldar.order.domain.repository.ReviewRepository;
import com.kaldar.kaldar.order.domain.model.OrderEntity;
import com.kaldar.kaldar.order.domain.model.ReviewEntity;
import com.kaldar.kaldar.shared.domain.constants.Role;
import com.kaldar.kaldar.shared.domain.exceptions.DryCleanerEmailAlreadyExistException;
import com.kaldar.kaldar.shared.domain.exceptions.UserNotFoundException;
import com.kaldar.kaldar.shared.infrastructure.auth.domain.model.VerificationToken;
import com.kaldar.kaldar.shared.infrastructure.auth.domain.repository.VerificationTokenRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultDryCleanerService Unit Tests")
class DefaultDryCleanerServiceTest {

    @Mock private DryCleanerEntityRepository dryCleanerEntityRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private VerificationTokenRepository verificationTokenRepository;
    @Mock private EmailService emailService;
    @Mock private CustomerEntityRepository customerEntityRepository;
    @Mock private OrderEntityRepository orderEntityRepository;
    @Mock private ServiceOfferingRepository serviceOfferingRepository;
    @Mock private BusinessVerificationRepository businessVerificationRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private com.kaldar.kaldar.wallet.domain.repository.WalletRepository walletRepository;

    private DefaultDryCleanerService dryCleanerService;

    @BeforeEach
    void setUp() {
        dryCleanerService = new DefaultDryCleanerService(
                dryCleanerEntityRepository, passwordEncoder, verificationTokenRepository,
                emailService, 6, 15, customerEntityRepository, orderEntityRepository,
                serviceOfferingRepository, businessVerificationRepository, reviewRepository,
                walletRepository
        );
    }

    // =========================================================================
    // Helper Factories
    // =========================================================================

    private DryCleanerRegistrationRequest buildRegistrationRequest() {
        DryCleanerRegistrationRequest req = new DryCleanerRegistrationRequest();
        req.setFirstName("Sarah");
        req.setLastName("Johnson");
        req.setEmail("sarah@example.com");
        req.setBusinessEmail("contact@sparklecleaners.com");
        req.setBusinessName("Sparkle Cleaners");
        req.setShopAddress("45 Commercial Avenue, Lagos");
        req.setBusinessPhoneNumber("+2348098765432");
        req.setPassword("SecurePass123!");
        return req;
    }

    private DryCleanerEntity buildDryCleanerEntity() {
        DryCleanerEntity entity = new DryCleanerEntity();
        entity.setId(1L);
        entity.setFirstName("Sarah");
        entity.setLastName("Johnson");
        entity.setEmail("sarah@example.com");
        entity.setBusinessName("Sparkle Cleaners");
        entity.setBusinessAddress("45 Commercial Avenue, Lagos");
        entity.setPhoneNumber("+2348098765432");
        entity.setPassword("$2a$encodedPassword");
        entity.setActive(true);
        entity.setServiceOfferings(new ArrayList<>());
        entity.getRoles().add(Role.DRY_CLEANER);
        return entity;
    }

    // =========================================================================
    // registerDryCleaner
    // =========================================================================

    @Nested
    @DisplayName("registerDryCleaner()")
    class RegisterDryCleaner {

        @Test
        @DisplayName("should register dry cleaner successfully and send OTP email")
        void shouldRegisterSuccessfully() {
            DryCleanerRegistrationRequest request = buildRegistrationRequest();
            when(dryCleanerEntityRepository.existsByEmail("sarah@example.com")).thenReturn(false);
            when(dryCleanerEntityRepository.existsByEmail("contact@sparklecleaners.com")).thenReturn(false);
            when(passwordEncoder.encode("SecurePass123!")).thenReturn("$2a$encoded");
            when(dryCleanerEntityRepository.save(any(DryCleanerEntity.class))).thenAnswer(inv -> {
                DryCleanerEntity e = inv.getArgument(0);
                e.setId(1L);
                return e;
            });
            when(verificationTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            SendVerificationEmailResponse emailResp = new SendVerificationEmailResponse();
            when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(emailResp);

            SendVerificationEmailResponse response = dryCleanerService.registerDryCleaner(request);

            assertThat(response).isNotNull();
            verify(dryCleanerEntityRepository).save(any(DryCleanerEntity.class));
            verify(verificationTokenRepository).save(any(VerificationToken.class));
            verify(emailService).sendVerificationEmail(eq("sarah@example.com"), anyString());
        }

        @Test
        @DisplayName("should throw DryCleanerEmailAlreadyExistException when personal email is duplicate")
        void shouldThrowWhenPersonalEmailExists() {
            DryCleanerRegistrationRequest request = buildRegistrationRequest();
            when(dryCleanerEntityRepository.existsByEmail("sarah@example.com")).thenReturn(true);

            assertThatThrownBy(() -> dryCleanerService.registerDryCleaner(request))
                    .isInstanceOf(DryCleanerEmailAlreadyExistException.class);

            verify(dryCleanerEntityRepository, never()).save(any());
            verifyNoInteractions(emailService);
        }

        @Test
        @DisplayName("should hash the password before saving")
        void shouldHashPasswordBeforeSaving() {
            DryCleanerRegistrationRequest request = buildRegistrationRequest();
            when(dryCleanerEntityRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode("SecurePass123!")).thenReturn("$2a$hashed");
            when(dryCleanerEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(emailService.sendVerificationEmail(anyString(), anyString()))
                    .thenReturn(new SendVerificationEmailResponse());

            dryCleanerService.registerDryCleaner(request);

            ArgumentCaptor<DryCleanerEntity> captor = ArgumentCaptor.forClass(DryCleanerEntity.class);
            verify(dryCleanerEntityRepository).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("$2a$hashed");
        }

        @Test
        @DisplayName("should mark dry cleaner as unverified on registration")
        void shouldSetVerifiedUserFalse() {
            DryCleanerRegistrationRequest request = buildRegistrationRequest();
            when(dryCleanerEntityRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$hashed");
            when(dryCleanerEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(emailService.sendVerificationEmail(anyString(), anyString()))
                    .thenReturn(new SendVerificationEmailResponse());

            dryCleanerService.registerDryCleaner(request);

            ArgumentCaptor<DryCleanerEntity> captor = ArgumentCaptor.forClass(DryCleanerEntity.class);
            verify(dryCleanerEntityRepository).save(captor.capture());
            assertThat(captor.getValue().isVerifiedUser()).isFalse();
        }

        @Test
        @DisplayName("should assign DRY_CLEANER role")
        void shouldAssignDryCleanerRole() {
            DryCleanerRegistrationRequest request = buildRegistrationRequest();
            when(dryCleanerEntityRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$hashed");
            when(dryCleanerEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(emailService.sendVerificationEmail(anyString(), anyString()))
                    .thenReturn(new SendVerificationEmailResponse());

            dryCleanerService.registerDryCleaner(request);

            ArgumentCaptor<DryCleanerEntity> captor = ArgumentCaptor.forClass(DryCleanerEntity.class);
            verify(dryCleanerEntityRepository).save(captor.capture());
            assertThat(captor.getValue().getRoles()).contains(Role.DRY_CLEANER);
        }
    }

    // =========================================================================
    // editProfile
    // =========================================================================

    @Nested
    @DisplayName("editProfile()")
    class EditProfile {

        @Test
        @DisplayName("should update profile fields successfully")
        void shouldUpdateProfileSuccessfully() {
            DryCleanerEntity existing = buildDryCleanerEntity();
            when(dryCleanerEntityRepository.findById(1L)).thenReturn(Optional.of(existing));

            UpdateDryCleanerProfileRequest request = new UpdateDryCleanerProfileRequest();
            request.setDryCleanerId(1L);
            request.setFirstName("Sara");
            request.setLastName("Williams");
            request.setBusinessName("Sara's Cleaners");
            request.setShopAddress("88 New Road, Abuja");
            request.setBusinessPhoneNumber("+2349022222222");
            request.setUpdatedAt(LocalDateTime.now());

            DryCleanerProfileResponse response = dryCleanerService.editProfile(request);

            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isNotBlank();
            assertThat(existing.getFirstName()).isEqualTo("Sara");
            assertThat(existing.getBusinessName()).isEqualTo("Sara's Cleaners");
        }

        @Test
        @DisplayName("should throw UserNotFoundException when dry cleaner does not exist")
        void shouldThrowWhenDryCleanerNotFound() {
            when(dryCleanerEntityRepository.findById(99L)).thenReturn(Optional.empty());

            UpdateDryCleanerProfileRequest request = new UpdateDryCleanerProfileRequest();
            request.setDryCleanerId(99L);

            assertThatThrownBy(() -> dryCleanerService.editProfile(request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    // =========================================================================
    // updatePayoutAccount
    // =========================================================================

    @Nested
    @DisplayName("updatePayoutAccount()")
    class UpdatePayoutAccount {

        @Test
        @DisplayName("should save payout account details successfully")
        void shouldUpdatePayoutAccount() {
            DryCleanerEntity existing = buildDryCleanerEntity();
            when(dryCleanerEntityRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(dryCleanerEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            dryCleanerService.updatePayoutAccount(1L, "Sparkle Cleaners Ltd", "1234567890", "058", "GTBank");

            ArgumentCaptor<DryCleanerEntity> captor = ArgumentCaptor.forClass(DryCleanerEntity.class);
            verify(dryCleanerEntityRepository).save(captor.capture());
            assertThat(captor.getValue().getAccountName()).isEqualTo("Sparkle Cleaners Ltd");
            assertThat(captor.getValue().getAccountNumber()).isEqualTo("1234567890");
            assertThat(captor.getValue().getBankCode()).isEqualTo("058");
            assertThat(captor.getValue().getBankName()).isEqualTo("GTBank");
        }

        @Test
        @DisplayName("should throw UserNotFoundException when dry cleaner does not exist")
        void shouldThrowWhenDryCleanerNotFound() {
            when(dryCleanerEntityRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dryCleanerService.updatePayoutAccount(99L, "Name", "1234567890", "058", "GTBank"))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    // =========================================================================
    // updateWorkingHours
    // =========================================================================

    @Nested
    @DisplayName("updateWorkingHours()")
    class UpdateWorkingHours {

        @Test
        @DisplayName("should save working hours JSON successfully")
        void shouldSaveWorkingHours() {
            DryCleanerEntity existing = buildDryCleanerEntity();
            when(dryCleanerEntityRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(dryCleanerEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            String workingHoursJson = "{\"monday\":{\"open\":\"08:00\",\"close\":\"18:00\"}}";
            dryCleanerService.updateWorkingHours(1L, workingHoursJson);

            ArgumentCaptor<DryCleanerEntity> captor = ArgumentCaptor.forClass(DryCleanerEntity.class);
            verify(dryCleanerEntityRepository).save(captor.capture());
            assertThat(captor.getValue().getWorkingHours()).isEqualTo(workingHoursJson);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when dry cleaner does not exist")
        void shouldThrowWhenDryCleanerNotFound() {
            when(dryCleanerEntityRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dryCleanerService.updateWorkingHours(99L, "{}"))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    // =========================================================================
    // getOnboardingStatus
    // =========================================================================

    @Nested
    @DisplayName("getOnboardingStatus()")
    class GetOnboardingStatus {

        @Test
        @DisplayName("should return fully incomplete onboarding when nothing is set")
        void shouldReturnAllFalseWhenNotSetup() {
            DryCleanerEntity entity = new DryCleanerEntity();
            entity.setId(1L);
            entity.setBusinessName(""); // empty — not setup
            entity.setServiceOfferings(new ArrayList<>());

            when(dryCleanerEntityRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(businessVerificationRepository.findByDryCleanerId(1L)).thenReturn(Optional.empty());

            OnboardingStatusResponse status = dryCleanerService.getOnboardingStatus(1L);

            assertThat(status.isBusinessVerified()).isFalse();
            assertThat(status.isStoreProfileSetup()).isFalse();
            assertThat(status.isPayoutAccountAdded()).isFalse();
            assertThat(status.isBusinessOperationsSetup()).isFalse();
            assertThat(status.isStoreInventorySetup()).isFalse();
        }

        @Test
        @DisplayName("should return correct status when all steps are complete")
        void shouldReturnAllTrueWhenFullySetup() {
            DryCleanerEntity entity = buildDryCleanerEntity();
            entity.setBusinessName("Sparkle Cleaners");
            entity.setAccountNumber("1234567890");
            entity.setWorkingHours("{\"monday\":{}}");
            ServiceOffering offering = new ServiceOffering();
            offering.setId(1L);
            entity.setServiceOfferings(List.of(offering));

            when(dryCleanerEntityRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(businessVerificationRepository.findByDryCleanerId(1L))
                    .thenReturn(Optional.of(new com.kaldar.kaldar.drycleaner.domain.model.BusinessVerification()));

            OnboardingStatusResponse status = dryCleanerService.getOnboardingStatus(1L);

            assertThat(status.isBusinessVerified()).isTrue();
            assertThat(status.isStoreProfileSetup()).isTrue();
            assertThat(status.isPayoutAccountAdded()).isTrue();
            assertThat(status.isBusinessOperationsSetup()).isTrue();
            assertThat(status.isStoreInventorySetup()).isTrue();
        }

        @Test
        @DisplayName("should throw UserNotFoundException when dry cleaner does not exist")
        void shouldThrowWhenDryCleanerNotFound() {
            when(dryCleanerEntityRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dryCleanerService.getOnboardingStatus(99L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    // =========================================================================
    // addOrUpdateService
    // =========================================================================

    @Nested
    @DisplayName("addOrUpdateService()")
    class AddOrUpdateService {

        @Test
        @DisplayName("should add a new service offering when none exists")
        void shouldAddNewService() {
            DryCleanerEntity dryCleaner = buildDryCleanerEntity();
            when(dryCleanerEntityRepository.findById(1L)).thenReturn(Optional.of(dryCleaner));
            when(serviceOfferingRepository.findByDryCleanerIdAndServiceName(1L, "Shirt Cleaning"))
                    .thenReturn(Optional.empty());
            when(serviceOfferingRepository.save(any())).thenAnswer(inv -> {
                ServiceOffering s = inv.getArgument(0);
                s.setId(100L);
                return s;
            });

            ServiceOfferingRequest request = new ServiceOfferingRequest();
            request.setServiceName("Shirt Cleaning");
            request.setUnitPrice(1500.0);
            request.setDescription("Professional shirt cleaning");

            ServiceOfferingResponse response = dryCleanerService.addOrUpdateService(1L, request);

            assertThat(response.getId()).isEqualTo(100L);
            assertThat(response.getServiceName()).isEqualTo("Shirt Cleaning");
            assertThat(response.getUnitPrice()).isEqualTo(1500.0);
        }

        @Test
        @DisplayName("should update existing service offering price and description")
        void shouldUpdateExistingService() {
            DryCleanerEntity dryCleaner = buildDryCleanerEntity();
            ServiceOffering existing = new ServiceOffering();
            existing.setId(50L);
            existing.setServiceName("Shirt Cleaning");
            existing.setUnitPrice(1000.0);
            existing.setDryCleaner(dryCleaner);

            when(dryCleanerEntityRepository.findById(1L)).thenReturn(Optional.of(dryCleaner));
            when(serviceOfferingRepository.findByDryCleanerIdAndServiceName(1L, "Shirt Cleaning"))
                    .thenReturn(Optional.of(existing));
            when(serviceOfferingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            ServiceOfferingRequest request = new ServiceOfferingRequest();
            request.setServiceName("Shirt Cleaning");
            request.setUnitPrice(2000.0); // updated price
            request.setDescription("Premium shirt cleaning");

            ServiceOfferingResponse response = dryCleanerService.addOrUpdateService(1L, request);

            assertThat(response.getUnitPrice()).isEqualTo(2000.0);
            assertThat(response.getDescription()).isEqualTo("Premium shirt cleaning");
        }

        @Test
        @DisplayName("should throw UserNotFoundException when dry cleaner does not exist")
        void shouldThrowWhenDryCleanerNotFound() {
            when(dryCleanerEntityRepository.findById(99L)).thenReturn(Optional.empty());

            ServiceOfferingRequest request = new ServiceOfferingRequest();
            request.setServiceName("Shirt Cleaning");
            request.setUnitPrice(1500.0);

            assertThatThrownBy(() -> dryCleanerService.addOrUpdateService(99L, request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    // =========================================================================
    // getAnalytics
    // =========================================================================

    @Nested
    @DisplayName("getAnalytics()")
    class GetAnalytics {

        @Test
        @DisplayName("should return analytics with revenue and order count for 'today' period")
        void shouldReturnAnalyticsForToday() {
            OrderEntity order = new OrderEntity();
            order.setId(1L);
            order.setTotalAmount(5000.0);
            order.setCreatedAt(LocalDateTime.now()); // today

            when(orderEntityRepository.findByDryCleanerIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(order));
            when(reviewRepository.findByDryCleanerIdOrderByCreatedAtDesc(1L)).thenReturn(new ArrayList<>());

            AnalyticsResponse analytics = dryCleanerService.getAnalytics(1L, "today");

            assertThat(analytics).isNotNull();
            assertThat(analytics.getRevenue()).isEqualByComparingTo(BigDecimal.valueOf(5000.0));
            assertThat(analytics.getOrders()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should return zero revenue when dry cleaner has no orders today")
        void shouldReturnZeroRevenueForNoOrders() {
            // Past order — should not be included in 'today'
            OrderEntity oldOrder = new OrderEntity();
            oldOrder.setId(1L);
            oldOrder.setTotalAmount(5000.0);
            oldOrder.setCreatedAt(LocalDateTime.now().minusDays(5));

            when(orderEntityRepository.findByDryCleanerIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(oldOrder));
            when(reviewRepository.findByDryCleanerIdOrderByCreatedAtDesc(1L)).thenReturn(new ArrayList<>());

            AnalyticsResponse analytics = dryCleanerService.getAnalytics(1L, "today");

            assertThat(analytics.getOrders()).isEqualTo(0L);
            assertThat(analytics.getRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("should calculate average rating correctly")
        void shouldCalculateAvgRating() {
            when(orderEntityRepository.findByDryCleanerIdOrderByCreatedAtDesc(1L)).thenReturn(new ArrayList<>());

            ReviewEntity r1 = new ReviewEntity(); r1.setRating(5);
            ReviewEntity r2 = new ReviewEntity(); r2.setRating(3);
            when(reviewRepository.findByDryCleanerIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(r1, r2));

            AnalyticsResponse analytics = dryCleanerService.getAnalytics(1L, "month");

            assertThat(analytics.getAvgRating()).isEqualTo(4.0);
        }
    }

    // =========================================================================
    // getServices
    // =========================================================================

    @Nested
    @DisplayName("getServices()")
    class GetServices {

        @Test
        @DisplayName("should return all service offerings for a dry cleaner")
        void shouldReturnServices() {
            ServiceOffering s1 = new ServiceOffering();
            s1.setId(1L); s1.setServiceName("Shirt Cleaning"); s1.setUnitPrice(1500.0);
            ServiceOffering s2 = new ServiceOffering();
            s2.setId(2L); s2.setServiceName("Suit Cleaning"); s2.setUnitPrice(3500.0);

            when(serviceOfferingRepository.findByDryCleanerId(1L)).thenReturn(List.of(s1, s2));

            List<ServiceOfferingResponse> services = dryCleanerService.getServices(1L);

            assertThat(services).hasSize(2);
            assertThat(services.get(0).getServiceName()).isEqualTo("Shirt Cleaning");
            assertThat(services.get(1).getUnitPrice()).isEqualTo(3500.0);
        }

        @Test
        @DisplayName("should return empty list when no services configured")
        void shouldReturnEmptyListWhenNoServices() {
            when(serviceOfferingRepository.findByDryCleanerId(1L)).thenReturn(new ArrayList<>());

            List<ServiceOfferingResponse> services = dryCleanerService.getServices(1L);

            assertThat(services).isEmpty();
        }
    }
}
