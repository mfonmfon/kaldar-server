package com.kaldar.kaldar;

import com.kaldar.kaldar.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.dtos.request.ChangePasswordRequest;
import com.kaldar.kaldar.dtos.request.CustomerRegistrationRequest;
import com.kaldar.kaldar.dtos.request.UpdateCustomerProfileRequest;
import com.kaldar.kaldar.dtos.response.ChangePasswordResponse;
import com.kaldar.kaldar.dtos.response.CustomerProfileResponse;
import com.kaldar.kaldar.dtos.response.CustomerRegistrationResponse;
import com.kaldar.kaldar.kaldarService.interfaces.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerEntityRepository customerEntityRepository;


    @Test
    public void testThatCustomersCanRegister(){
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest();
        customerRegistrationRequest.setFirstName("Alexander");
        customerRegistrationRequest.setLastName("Ovo");
        customerRegistrationRequest.setEmail("alexawyahrota@gmail.com");
        customerRegistrationRequest.setPhoneNumber("08147995494");
        customerRegistrationRequest.setAddress("Lagos, Nigeria");
        customerRegistrationRequest.setPassword("123Alex!");
        CustomerRegistrationResponse sendVerificationEmailResponse = customerService.registerCustomer(customerRegistrationRequest);
        assertThat(sendVerificationEmailResponse).isNotNull();

    }

    @Test
    public void testThatCustomerCanBeUpdateProfile(){
        Long customerId = 1L;
        UpdateCustomerProfileRequest updateCustomerProfileRequest = new UpdateCustomerProfileRequest();
        updateCustomerProfileRequest.setCustomerId(customerId);
        updateCustomerProfileRequest.setFirstName("");
        updateCustomerProfileRequest.setLastName("");
        updateCustomerProfileRequest.setPhoneNumber("");
        updateCustomerProfileRequest.setDefaultAddress("");
        CustomerProfileResponse customerProfileResponse = customerService.updateCustomerProfile(updateCustomerProfileRequest);
        assertThat(customerProfileResponse).isNotNull();
    }

    @Test
    public void testThatCan_GetCustomerProfile(){
        Long customerId = 1L;
        CustomerProfileResponse customerProfileResponse = customerService.getCustomerProfile(customerId);
        assertThat(customerProfileResponse).isNotNull();
    }

    @Test
    public void testThatCan_ChangePassword(){
        Long customerId = 1L;
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCustomerId(customerId);
        changePasswordRequest.setOldPassword("");
        changePasswordRequest.setNewPassword("");
        changePasswordRequest.setTimestamp(LocalDateTime.now());
        customerService.changePassword(changePasswordRequest);
    }


}
