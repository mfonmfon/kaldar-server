package com.kaldar.kaldar;

import com.kaldar.kaldar.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.dtos.request.CustomerRegistrationRequest;
import com.kaldar.kaldar.dtos.response.AuthenticationResponse;
import com.kaldar.kaldar.dtos.response.CustomerRegistrationResponse;
import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.kaldarService.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
}
