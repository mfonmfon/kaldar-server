package com.kaldar.kaldar.kaldarService;

import com.kaldar.kaldar.dtos.request.CustomerRegistrationRequest;
import com.kaldar.kaldar.dtos.response.AuthenticationResponse;
import com.kaldar.kaldar.dtos.response.CustomerRegistrationResponse;
import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;

public interface CustomerService {
    CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest customerRegistrationRequest);

}
