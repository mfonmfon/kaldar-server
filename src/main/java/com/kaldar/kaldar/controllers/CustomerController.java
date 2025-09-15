package com.kaldar.kaldar.controllers;

import com.kaldar.kaldar.dtos.request.CustomerRegistrationRequest;
import com.kaldar.kaldar.dtos.request.UpdateCustomerProfileRequest;
import com.kaldar.kaldar.dtos.response.ApiResponse;
import com.kaldar.kaldar.dtos.response.CustomerProfileResponse;
import com.kaldar.kaldar.dtos.response.CustomerRegistrationResponse;
import com.kaldar.kaldar.kaldarService.interfaces.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kaldar.kaldar.contants.StatusResponse.CUSTOMER_PROFILE_UPDATE_STATUS_MESSAGE;
import static com.kaldar.kaldar.contants.StatusResponse.CUSTOMER_REGISTRATION_SUCCESS_MESSAGE;

@RestController
@RequestMapping("api/v1/auth/")
public class CustomerController{

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerRegistrationResponse>> register(@RequestBody CustomerRegistrationRequest customerRegistrationRequest){
        CustomerRegistrationResponse customerRegistrationResponse = customerService.registerCustomer(customerRegistrationRequest);
        ApiResponse<CustomerRegistrationResponse> apiResponse = ApiResponse.<CustomerRegistrationResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.CREATED.value())
                .message(CUSTOMER_REGISTRATION_SUCCESS_MESSAGE.getMessage())
                .data(customerRegistrationResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/updated_customer_profile")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateCustomerProfile(
            @RequestBody UpdateCustomerProfileRequest customerProfileRequest){
        CustomerProfileResponse customerProfileResponse = customerService.updateCustomerProfile(customerProfileRequest);
        ApiResponse<CustomerProfileResponse> apiResponse = ApiResponse.<CustomerProfileResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(CUSTOMER_PROFILE_UPDATE_STATUS_MESSAGE.getMessage())
                .data(customerProfileResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
     }

     @GetMapping("/{customer_id}")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getProfile(Long customerId){
        CustomerProfileResponse customerProfileResponse = customerService.getCustomerProfile(customerId);
        ApiResponse<CustomerProfileResponse> apiResponse = ApiResponse.<CustomerProfileResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(CUSTOMER_PROFILE_UPDATE_STATUS_MESSAGE.getMessage())
                .data(customerProfileResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
     }



}