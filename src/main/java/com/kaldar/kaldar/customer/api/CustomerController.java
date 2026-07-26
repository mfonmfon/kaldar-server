package com.kaldar.kaldar.customer.api;

import com.kaldar.kaldar.customer.application.dto.request.ChangePasswordRequest;
import com.kaldar.kaldar.customer.application.dto.request.CustomerRegistrationRequest;
import com.kaldar.kaldar.customer.application.dto.request.UpdateCustomerProfileRequest;
import com.kaldar.kaldar.customer.application.dto.response.ChangePasswordResponse;
import com.kaldar.kaldar.customer.application.dto.response.CustomerProfileResponse;
import com.kaldar.kaldar.customer.application.dto.response.CustomerRegistrationResponse;
import com.kaldar.kaldar.customer.application.service.CustomerService;
import com.kaldar.kaldar.shared.api.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.CUSTOMER_PROFILE_UPDATE_STATUS_MESSAGE;
import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.CUSTOMER_REGISTRATION_SUCCESS_MESSAGE;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerRegistrationResponse>> register(
            @RequestBody CustomerRegistrationRequest customerRegistrationRequest) {
        CustomerRegistrationResponse customerRegistrationResponse = customerService
                .registerCustomer(customerRegistrationRequest);
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
            @RequestBody UpdateCustomerProfileRequest customerProfileRequest) {
        CustomerProfileResponse customerProfileResponse = customerService.updateCustomerProfile(customerProfileRequest);
        ApiResponse<CustomerProfileResponse> apiResponse = ApiResponse.<CustomerProfileResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(CUSTOMER_PROFILE_UPDATE_STATUS_MESSAGE.getMessage())
                .data(customerProfileResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getProfile(@PathVariable Long customerId) {
        CustomerProfileResponse customerProfileResponse = customerService.getCustomerProfile(customerId);
        ApiResponse<CustomerProfileResponse> apiResponse = ApiResponse.<CustomerProfileResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(CUSTOMER_PROFILE_UPDATE_STATUS_MESSAGE.getMessage())
                .data(customerProfileResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping("/change_password")
    public ResponseEntity<ApiResponse<ChangePasswordResponse>> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        ChangePasswordResponse changePasswordResponse = customerService.changePassword(changePasswordRequest);
        ApiResponse<ChangePasswordResponse> apiResponse = ApiResponse.<ChangePasswordResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(changePasswordResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Customer account deleted successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}