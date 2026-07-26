package com.kaldar.kaldar;

import com.kaldar.kaldar.drycleaner.application.dto.request.DryCleanerRegistrationRequest;
import com.kaldar.kaldar.drycleaner.application.dto.response.AnalyticsResponse;
import com.kaldar.kaldar.drycleaner.application.dto.response.OnboardingStatusResponse;
import com.kaldar.kaldar.drycleaner.application.service.impl.DefaultDryCleanerService;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import com.kaldar.kaldar.drycleaner.domain.repository.BusinessVerificationRepository;
import com.kaldar.kaldar.drycleaner.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.drycleaner.domain.repository.ServiceOfferingRepository;
import com.kaldar.kaldar.order.domain.repository.OrderEntityRepository;
import com.kaldar.kaldar.order.domain.repository.ReviewRepository;
import com.kaldar.kaldar.shared.infrastructure.auth.domain.repository.VerificationTokenRepository;
import com.kaldar.kaldar.shared.infrastructure.email.service.EmailService;
import com.kaldar.kaldar.customer.domain.repository.CustomerEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DryCleanerServiceUnitTest {

    private DefaultDryCleanerService dryCleanerService;

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

    @BeforeEach
    void setUp() {
        dryCleanerService = new DefaultDryCleanerService(
                dryCleanerEntityRepository, passwordEncoder, verificationTokenRepository,
                emailService, 6, 10, customerEntityRepository,
                orderEntityRepository,
                serviceOfferingRepository, businessVerificationRepository, reviewRepository,
                walletRepository
        );
    }

    @Test
    void testGetOnboardingStatus() {
        Long id = 1L;
        DryCleanerEntity entity = new DryCleanerEntity();
        entity.setBusinessName("Test");
        entity.setAccountNumber("123");
        entity.setWorkingHours("{}");

        when(dryCleanerEntityRepository.findById(id)).thenReturn(Optional.of(entity));
        when(businessVerificationRepository.findByDryCleanerId(id)).thenReturn(Optional.empty());

        OnboardingStatusResponse status = dryCleanerService.getOnboardingStatus(id);

        assertThat(status).isNotNull();
        assertThat(status.isStoreProfileSetup()).isTrue();
        assertThat(status.isPayoutAccountAdded()).isTrue();
        assertThat(status.isBusinessVerified()).isFalse();
    }

    @Test
    void testGetAnalytics() {
        Long id = 1L;
        when(orderEntityRepository.findByDryCleanerIdOrderByCreatedAtDesc(id)).thenReturn(Collections.emptyList());
        when(reviewRepository.findByDryCleanerIdOrderByCreatedAtDesc(id)).thenReturn(Collections.emptyList());

        AnalyticsResponse analytics = dryCleanerService.getAnalytics(id, "today");

        assertThat(analytics).isNotNull();
        assertThat(analytics.getRevenue()).isEqualTo(BigDecimal.ZERO);
        assertThat(analytics.getOrders()).isEqualTo(0L);
    }
}
