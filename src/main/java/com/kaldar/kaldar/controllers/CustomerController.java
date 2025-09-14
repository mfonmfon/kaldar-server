package com.kaldar.kaldar.controllers;

import com.kaldar.kaldar.dtos.request.CustomerRegistrationRequest;
import com.kaldar.kaldar.dtos.response.ApiResponse;
import com.kaldar.kaldar.dtos.response.CustomerRegistrationResponse;
import com.kaldar.kaldar.kaldarService.interfaces.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.kaldar.kaldar.contants.StatusResponse.CUSTOMER_REGISTRATION_SUCCESS_MESSAGE;

@RestController
@RequestMapping("api/v1/customer")
public class CustomerController{

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerRegistrationResponse>> register(CustomerRegistrationRequest customerRegistrationRequest){
        CustomerRegistrationResponse customerRegistrationResponse = customerService.registerCustomer(customerRegistrationRequest);
        ApiResponse<CustomerRegistrationResponse> apiResponse = ApiResponse.<CustomerRegistrationResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.CREATED.value())
                .message(CUSTOMER_REGISTRATION_SUCCESS_MESSAGE.getMessage())
                .data(customerRegistrationResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }



}