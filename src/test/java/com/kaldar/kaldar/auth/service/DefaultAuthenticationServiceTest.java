package com.kaldar.kaldar.auth.service;

import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import com.kaldar.kaldar.shared.domain.constants.Role;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SessionResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.service.impl.DefaultAuthenticationService;
import com.kaldar.kaldar.shared.infrastructure.auth.service.impl.JwtService;
import com.kaldar.kaldar.shared.infrastructure.utility.CurrentUserResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultAuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CurrentUserResolver currentUserResolver;

    private DefaultAuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new DefaultAuthenticationService(authenticationManager, jwtService, currentUserResolver);
    }

    @Test
    @DisplayName("Should return customer session details when authenticated user is a Customer")
    void shouldReturnCustomerSession() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(42L);
        customer.setEmail("customer+test@kaldar.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.getRoles().add(Role.CUSTOMER);

        when(currentUserResolver.getCurrentUser()).thenReturn(customer);

        SessionResponse response = authenticationService.getSessionInfo();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(42L);
        assertThat(response.getCustomerId()).isEqualTo(42L);
        assertThat(response.getDryCleanerId()).isNull();
        assertThat(response.getUserType()).isEqualTo("CUSTOMER");
        assertThat(response.getEmail()).isEqualTo("customer+test@kaldar.com");
    }

    @Test
    @DisplayName("Should return dry cleaner session details when authenticated user is a DryCleaner")
    void shouldReturnDryCleanerSession() {
        DryCleanerEntity dryCleaner = new DryCleanerEntity();
        dryCleaner.setId(99L);
        dryCleaner.setEmail("cleaner+test@kaldar.com");
        dryCleaner.setFirstName("Sparkle");
        dryCleaner.setLastName("Cleaners");
        dryCleaner.getRoles().add(Role.DRY_CLEANER);

        when(currentUserResolver.getCurrentUser()).thenReturn(dryCleaner);

        SessionResponse response = authenticationService.getSessionInfo();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(99L);
        assertThat(response.getDryCleanerId()).isEqualTo(99L);
        assertThat(response.getCustomerId()).isNull();
        assertThat(response.getUserType()).isEqualTo("DRY_CLEANER");
        assertThat(response.getEmail()).isEqualTo("cleaner+test@kaldar.com");
    }
}
