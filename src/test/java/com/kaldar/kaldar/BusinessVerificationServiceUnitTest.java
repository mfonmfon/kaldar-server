package com.kaldar.kaldar;

import com.kaldar.kaldar.drycleaner.application.dto.request.VerifyBusinessRequest;
import com.kaldar.kaldar.drycleaner.application.dto.response.VerifyBusinessResponse;
import com.kaldar.kaldar.drycleaner.application.service.impl.DefaultBusinessVerificationService;
import com.kaldar.kaldar.drycleaner.domain.model.BusinessVerification;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import com.kaldar.kaldar.drycleaner.domain.repository.BusinessVerificationRepository;
import com.kaldar.kaldar.drycleaner.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.shared.domain.constants.BusinessType;
import com.kaldar.kaldar.shared.domain.constants.VerificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusinessVerificationServiceUnitTest {

    private DefaultBusinessVerificationService verificationService;

    @Mock private BusinessVerificationRepository businessVerificationRepository;
    @Mock private DryCleanerEntityRepository dryCleanerEntityRepository;

    @BeforeEach
    void setUp() {
        verificationService = new DefaultBusinessVerificationService(
                businessVerificationRepository, dryCleanerEntityRepository
        );
    }

    @Test
    void testSubmitBusinessVerification() {
        Long dryCleanerId = 1L;
        VerifyBusinessRequest request = new VerifyBusinessRequest();
        request.setDryCleanerId(dryCleanerId);
        request.setBusinessName("Test Biz");
        request.setBusinessType(BusinessType.SOLE_PROPRIETORSHIP);
        request.setRegistrationNumber("RC123");
        request.setTaxIdentificationNumber("TIN123");
        
        DryCleanerEntity dryCleaner = new DryCleanerEntity();
        dryCleaner.setId(dryCleanerId);

        when(dryCleanerEntityRepository.findById(dryCleanerId)).thenReturn(Optional.of(dryCleaner));
        when(businessVerificationRepository.findByDryCleanerId(dryCleanerId)).thenReturn(Optional.empty());
        when(businessVerificationRepository.existsByRegistrationNumber(anyString())).thenReturn(false);
        when(businessVerificationRepository.existsByTaxIdentificationNumber(anyString())).thenReturn(false);
        
        when(businessVerificationRepository.save(any())).thenAnswer(i -> {
            BusinessVerification bv = i.getArgument(0);
            bv.setId(100L);
            return bv;
        });

        VerifyBusinessResponse response = verificationService.submitBusinessVerification(request, "http://docs.com/cac.pdf");

        assertThat(response).isNotNull();
        assertThat(response.getVerificationStatus()).isEqualTo(VerificationStatus.PENDING);
        assertThat(response.getBusinessName()).isEqualTo("Test Biz");
    }
}
