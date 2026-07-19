package com.kaldar.kaldar.customer.service;

import com.kaldar.kaldar.customer.application.dto.request.ChangePasswordRequest;
import com.kaldar.kaldar.customer.application.dto.request.CustomerRegistrationRequest;
import com.kaldar.kaldar.customer.application.dto.request.UpdateCustomerProfileRequest;
import com.kaldar.kaldar.customer.application.dto.response.ChangePasswordResponse;
import com.kaldar.kaldar.customer.application.dto.response.CustomerProfileResponse;
import com.kaldar.kaldar.customer.application.dto.response.CustomerRegistrationResponse;
import com.kaldar.kaldar.customer.application.service.impl.DefaultCustomerService;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultCustomerService Unit Tests")
class DefaultCustomerServiceTest {

    @Mock
    private CustomerEntityRepository customerEntityRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;

    private DefaultCustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new DefaultCustomerService(
                customerEntityRepository, jwtService, verificationTokenRepository,
                emailService, passwordEncoder, 6, 15
        );
    }

    // =========================================================================
    // Helper Factories
    // =========================================================================

    private CustomerRegistrationRequest buildRegistrationRequest() {
        CustomerRegistrationRequest req = new CustomerRegistrationRequest();
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setEmail("john@example.com");
        req.setPassword("Password123!");
        req.setPhoneNumber("+2348012345678");
        req.setAddress("123 Main Street, Lagos");
        return req;
    }

    private CustomerEntity buildCustomerEntity() {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(1L);
        entity.setFirstName("John");
        entity.setLastName("Doe");
        entity.setEmail("john@example.com");
        entity.setPhoneNumber("+2348012345678");
        entity.setDefaultAddress("123 Main Street, Lagos");
        entity.setPassword("$2a$encodedPassword");
        entity.getRoles().add(Role.CUSTOMER);
        return entity;
    }

    // =========================================================================
    // registerCustomer
    // =========================================================================

    @Nested
    @DisplayName("registerCustomer()")
    class RegisterCustomer {

        @Test
        @DisplayName("should register customer successfully and send OTP email")
        void shouldRegisterCustomerSuccessfully() {
            // Arrange
            CustomerRegistrationRequest request = buildRegistrationRequest();
            when(customerEntityRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(passwordEncoder.encode("Password123!")).thenReturn("$2a$encoded");
            when(customerEntityRepository.save(any(CustomerEntity.class))).thenAnswer(inv -> {
                CustomerEntity e = inv.getArgument(0);
                e.setId(1L);
                return e;
            });
            when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(inv -> inv.getArgument(0));
            SendVerificationEmailResponse emailResp = new SendVerificationEmailResponse();
            when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(emailResp);

            // Act
            CustomerRegistrationResponse response = customerService.registerCustomer(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isNotBlank();
            verify(customerEntityRepository).existsByEmail("john@example.com");
            verify(passwordEncoder).encode("Password123!");
            verify(customerEntityRepository).save(any(CustomerEntity.class));
            verify(verificationTokenRepository).save(any(VerificationToken.class));
            verify(emailService).sendVerificationEmail(eq("john@example.com"), anyString());
        }

        @Test
        @DisplayName("should throw CustomerEmailAlreadyExist when email is duplicate")
        void shouldThrowWhenEmailAlreadyExists() {
            CustomerRegistrationRequest request = buildRegistrationRequest();
            when(customerEntityRepository.existsByEmail("john@example.com")).thenReturn(true);

            assertThatThrownBy(() -> customerService.registerCustomer(request))
                    .isInstanceOf(CustomerEmailAlreadyExist.class);

            verify(customerEntityRepository, never()).save(any());
            verifyNoInteractions(emailService);
        }

        @Test
        @DisplayName("should hash the password before saving — never store plaintext")
        void shouldHashPasswordBeforeSaving() {
            CustomerRegistrationRequest request = buildRegistrationRequest();
            when(customerEntityRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode("Password123!")).thenReturn("$2a$hashed");
            when(customerEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(emailService.sendVerificationEmail(anyString(), anyString()))
                    .thenReturn(new SendVerificationEmailResponse());

            customerService.registerCustomer(request);

            ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
            verify(customerEntityRepository).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("$2a$hashed");
            assertThat(captor.getValue().getPassword()).doesNotContain("Password123!");
        }

        @Test
        @DisplayName("should assign CUSTOMER role to newly registered customer")
        void shouldAssignCustomerRole() {
            CustomerRegistrationRequest request = buildRegistrationRequest();
            when(customerEntityRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$hashed");
            when(customerEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(emailService.sendVerificationEmail(anyString(), anyString()))
                    .thenReturn(new SendVerificationEmailResponse());

            customerService.registerCustomer(request);

            ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
            verify(customerEntityRepository).save(captor.capture());
            assertThat(captor.getValue().getRoles()).contains(Role.CUSTOMER);
        }
    }

    // =========================================================================
    // getCustomerProfile
    // =========================================================================

    @Nested
    @DisplayName("getCustomerProfile()")
    class GetCustomerProfile {

        @Test
        @DisplayName("should return correct profile for existing customer")
        void shouldReturnProfileForExistingCustomer() {
            CustomerEntity entity = buildCustomerEntity();
            when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(entity));

            CustomerProfileResponse response = customerService.getCustomerProfile(1L);

            assertThat(response).isNotNull();
            assertThat(response.getFirstName()).isEqualTo("John");
            assertThat(response.getLastName()).isEqualTo("Doe");
            assertThat(response.getEmail()).isNull(); // email is not set in buildCustomerProfileResponse
            assertThat(response.getPhoneNumber()).isEqualTo("+2348012345678");
            assertThat(response.getAddress()).isEqualTo("123 Main Street, Lagos");
        }

        @Test
        @DisplayName("should throw UserNotFoundException when customer does not exist")
        void shouldThrowWhenCustomerNotFound() {
            when(customerEntityRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.getCustomerProfile(99L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    // =========================================================================
    // updateCustomerProfile
    // =========================================================================

    @Nested
    @DisplayName("updateCustomerProfile()")
    class UpdateCustomerProfile {

        @Test
        @DisplayName("should update all profile fields and save entity")
        void shouldUpdateProfileFields() {
            CustomerEntity existing = buildCustomerEntity();
            when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(customerEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            UpdateCustomerProfileRequest request = new UpdateCustomerProfileRequest();
            request.setCustomerId(1L);
            request.setFirstName("Jane");
            request.setLastName("Smith");
            request.setPhoneNumber("+2349011111111");
            request.setDefaultAddress("456 New Road, Abuja");

            CustomerProfileResponse response = customerService.updateCustomerProfile(request);

            assertThat(response.getFirstName()).isEqualTo("Jane");
            assertThat(response.getLastName()).isEqualTo("Smith");
            assertThat(response.getPhoneNumber()).isEqualTo("+2349011111111");
            assertThat(response.getAddress()).isEqualTo("456 New Road, Abuja");

            ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
            verify(customerEntityRepository).save(captor.capture());
            assertThat(captor.getValue().getFirstName()).isEqualTo("Jane");
        }

        @Test
        @DisplayName("should throw UserNotFoundException when customer does not exist")
        void shouldThrowWhenCustomerNotFound() {
            when(customerEntityRepository.findById(99L)).thenReturn(Optional.empty());

            UpdateCustomerProfileRequest request = new UpdateCustomerProfileRequest();
            request.setCustomerId(99L);

            assertThatThrownBy(() -> customerService.updateCustomerProfile(request))
                    .isInstanceOf(UserNotFoundException.class);
            verify(customerEntityRepository, never()).save(any());
        }
    }

    // =========================================================================
    // changePassword
    // =========================================================================

    @Nested
    @DisplayName("changePassword()")
    class ChangePassword {

        @Test
        @DisplayName("should change password successfully when old password matches")
        void shouldChangePasswordSuccessfully() {
            CustomerEntity entity = buildCustomerEntity();
            when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(passwordEncoder.matches("OldPass123!", "$2a$encodedPassword")).thenReturn(true);
            when(passwordEncoder.encode("NewPass456!")).thenReturn("$2a$newHashed");
            when(customerEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCustomerId(1L);
            request.setOldPassword("OldPass123!");
            request.setNewPassword("NewPass456!");

            ChangePasswordResponse response = customerService.changePassword(request);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo("SUCCESS");

            ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
            verify(customerEntityRepository).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("$2a$newHashed");
        }

        @Test
        @DisplayName("should throw PasswordMismatchException when old password is wrong")
        void shouldThrowWhenOldPasswordDoesNotMatch() {
            CustomerEntity entity = buildCustomerEntity();
            when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(passwordEncoder.matches("WrongOldPass", "$2a$encodedPassword")).thenReturn(false);

            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCustomerId(1L);
            request.setOldPassword("WrongOldPass");
            request.setNewPassword("NewPass456!");

            assertThatThrownBy(() -> customerService.changePassword(request))
                    .isInstanceOf(PasswordMismatchException.class)
                    .hasMessageContaining("Password does not match");
            verify(customerEntityRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw EmptyRequiredFieldException when new password is blank")
        void shouldThrowWhenNewPasswordIsBlank() {
            CustomerEntity entity = buildCustomerEntity();
            when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCustomerId(1L);
            request.setOldPassword("OldPass123!");
            request.setNewPassword("   "); // blank

            assertThatThrownBy(() -> customerService.changePassword(request))
                    .isInstanceOf(EmptyRequiredFieldException.class)
                    .hasMessageContaining("Password can not be empty");
        }

        @Test
        @DisplayName("should throw EmptyRequiredFieldException when new password is null")
        void shouldThrowWhenNewPasswordIsNull() {
            CustomerEntity entity = buildCustomerEntity();
            when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCustomerId(1L);
            request.setOldPassword("OldPass123!");
            request.setNewPassword(null);

            assertThatThrownBy(() -> customerService.changePassword(request))
                    .isInstanceOf(EmptyRequiredFieldException.class);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when customer does not exist")
        void shouldThrowWhenCustomerNotFound() {
            when(customerEntityRepository.findById(99L)).thenReturn(Optional.empty());

            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCustomerId(99L);
            request.setOldPassword("OldPass");
            request.setNewPassword("NewPass");

            assertThatThrownBy(() -> customerService.changePassword(request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
