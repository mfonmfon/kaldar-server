package com.kaldar.kaldar;

import com.kaldar.kaldar.customer.application.dto.request.CustomerRegistrationRequest;
import com.kaldar.kaldar.customer.application.dto.request.UpdateCustomerProfileRequest;
import com.kaldar.kaldar.customer.application.dto.response.CustomerProfileResponse;
import com.kaldar.kaldar.customer.application.dto.response.CustomerRegistrationResponse;
import com.kaldar.kaldar.customer.application.service.impl.DefaultCustomerService;
import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.customer.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.shared.infrastructure.auth.domain.repository.VerificationTokenRepository;
import com.kaldar.kaldar.shared.infrastructure.auth.service.impl.JwtService;
import com.kaldar.kaldar.shared.infrastructure.email.service.EmailService;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SendVerificationEmailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceUnitTest {

    private DefaultCustomerService customerService;

    @Mock private CustomerEntityRepository customerEntityRepository;
    @Mock private JwtService jwtService;
    @Mock private VerificationTokenRepository verificationTokenRepository;
    @Mock private EmailService emailService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private com.kaldar.kaldar.wallet.domain.repository.WalletRepository walletRepository;

    @BeforeEach
    void setUp() {
        customerService = new DefaultCustomerService(
                customerEntityRepository, jwtService, verificationTokenRepository,
                emailService, passwordEncoder, walletRepository, 6, 15
        );
    }

    @Test
    void testRegisterCustomer() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPassword("password");

        when(customerEntityRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerEntityRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(new SendVerificationEmailResponse());

        CustomerRegistrationResponse response = customerService.registerCustomer(request);

        assertThat(response).isNotNull();
        verify(customerEntityRepository).save(any());
        verify(emailService).sendVerificationEmail(eq("john@example.com"), anyString());
    }

    @Test
    void testUpdateCustomerProfile() {
        Long id = 1L;
        UpdateCustomerProfileRequest request = new UpdateCustomerProfileRequest();
        request.setCustomerId(id);
        request.setFirstName("Jane");

        CustomerEntity customer = new CustomerEntity();
        customer.setId(id);

        when(customerEntityRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerEntityRepository.save(any())).thenReturn(customer);

        CustomerProfileResponse response = customerService.updateCustomerProfile(request);

        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("Jane");
    }
}
