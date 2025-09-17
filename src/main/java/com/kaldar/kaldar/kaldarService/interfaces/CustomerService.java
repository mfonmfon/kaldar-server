package com.kaldar.kaldar.kaldarService.interfaces;

import com.kaldar.kaldar.dtos.request.ChangePasswordRequest;
import com.kaldar.kaldar.dtos.request.CustomerRegistrationRequest;
import com.kaldar.kaldar.dtos.request.UpdateCustomerProfileRequest;
import com.kaldar.kaldar.dtos.response.ChangePasswordResponse;
import com.kaldar.kaldar.dtos.response.CustomerProfileResponse;
import com.kaldar.kaldar.dtos.response.CustomerRegistrationResponse;

public interface CustomerService {
    CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest customerRegistrationRequest);

    CustomerProfileResponse updateCustomerProfile(UpdateCustomerProfileRequest updateCustomerProfileRequest);

    CustomerProfileResponse getCustomerProfile(Long customerId);

    ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest);


}
