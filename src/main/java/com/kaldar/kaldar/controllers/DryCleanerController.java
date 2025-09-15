package com.kaldar.kaldar.controllers;
import com.kaldar.kaldar.dtos.request.AcceptOrderRequest;
import com.kaldar.kaldar.dtos.request.AcceptOrderResponse;
import com.kaldar.kaldar.dtos.request.DryCleanerRegistrationRequest;
import com.kaldar.kaldar.dtos.response.ApiResponse;
import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.kaldarService.interfaces.DryCleanerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kaldar.kaldar.contants.StatusResponse.ACCEPT_ORDER_SUCCESS_MESSAGE;
import static com.kaldar.kaldar.contants.StatusResponse.DRY_CLEANER_REGISTRATION_SUCCESS_MESSAGE;
import static java.util.stream.DoubleStream.builder;

@RestController
@RequestMapping("/api/v1/drycleaner")
public class DryCleanerController {

    private final DryCleanerService dryCleanerService;

    public DryCleanerController(DryCleanerService dryCleanerService) {
        this.dryCleanerService = dryCleanerService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<SendVerificationEmailResponse>> register(
            @RequestBody @Valid DryCleanerRegistrationRequest dryCleanerRegistrationRequest){
        SendVerificationEmailResponse dryCleanerRegistrationResponse = dryCleanerService.registerDryCleaner(dryCleanerRegistrationRequest);
        ApiResponse<SendVerificationEmailResponse> apiResponse = ApiResponse.<SendVerificationEmailResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.CREATED.value())
                .message(DRY_CLEANER_REGISTRATION_SUCCESS_MESSAGE.getMessage())
                .data(dryCleanerRegistrationResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AcceptOrderResponse>> acceptOrder(@RequestBody AcceptOrderRequest acceptOrderRequest){
        AcceptOrderResponse acceptOrderResponse = dryCleanerService.acceptOrder(acceptOrderRequest);
        ApiResponse<AcceptOrderResponse> apiResponse = ApiResponse.<AcceptOrderResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(ACCEPT_ORDER_SUCCESS_MESSAGE.getMessage())
                .data(acceptOrderResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
