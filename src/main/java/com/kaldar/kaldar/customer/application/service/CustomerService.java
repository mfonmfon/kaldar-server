package com.kaldar.kaldar.customer.application.service;

import com.kaldar.kaldar.customer.application.dto.request.ChangePasswordRequest;
import com.kaldar.kaldar.customer.application.dto.request.CustomerRegistrationRequest;
import com.kaldar.kaldar.customer.application.dto.request.UpdateCustomerProfileRequest;
import com.kaldar.kaldar.customer.application.dto.response.ChangePasswordResponse;
import com.kaldar.kaldar.customer.application.dto.response.CustomerProfileResponse;
import com.kaldar.kaldar.customer.application.dto.response.CustomerRegistrationResponse;

public interface CustomerService {
    CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest customerRegistrationRequest);

    CustomerProfileResponse updateCustomerProfile(UpdateCustomerProfileRequest updateCustomerProfileRequest);

    CustomerProfileResponse getCustomerProfile(Long customerId);

    ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest);

    void deleteCustomer(Long customerId);
}
