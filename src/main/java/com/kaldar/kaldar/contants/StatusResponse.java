package com.kaldar.kaldar.contants;

public enum StatusResponse {

    DRY_CLEANER_REGISTRATION_SUCCESS_MESSAGE("Account created. Please verify your email with the OTP sent"),
    VERIFICATION_TOKEN_SENT_MESSAGE("An OTP was has been sent to your email. Please verify it"),
    CUSTOMER_REGISTRATION_SUCCESS_MESSAGE("Registered customer successfully"),
    CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE("Customer not found"),
    OTP_NOT_FOUND_EXCEPTION_MESSAGE("OTP not found for this user"),
    EXPIRED_OTP_EXCEPTION_MESSAGE("OTP has expired"), INVALID_OTP_EXCEPTION("Invalid Otp"),
    VERIFICATION_OTP_SUCCESS_MESSAGE("Account verified successfully");

    private final    String message;

    StatusResponse(String message){
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

}
