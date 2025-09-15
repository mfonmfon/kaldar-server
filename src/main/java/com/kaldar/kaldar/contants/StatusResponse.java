package com.kaldar.kaldar.contants;

public enum StatusResponse {

    DRY_CLEANER_REGISTRATION_SUCCESS_MESSAGE("Account created. Please verify your email with the OTP sent"),
    VERIFICATION_TOKEN_SENT_MESSAGE("An OTP was has been sent to your email. Please verify it"),
    CUSTOMER_REGISTRATION_SUCCESS_MESSAGE("Registered customer successfully"),
    CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE("Customer not found"),
    OTP_NOT_FOUND_EXCEPTION_MESSAGE("OTP not found for this user"),
    EXPIRED_OTP_EXCEPTION_MESSAGE("OTP has expired"),
    INVALID_OTP_EXCEPTION("Invalid Otp"),
    VERIFICATION_OTP_SUCCESS_MESSAGE("Account verified successfully"),
    USER_ALREADY_VERIFIED_MESSAGE("User is already verified"),
    AUTHENTICATION_SUCCESS_MESSAGE("Login successfully"),
    ORDERS_NOT_FOUND_EXCEPTION_MESSAGE("Order not found "),
    DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE("Drycleaner not found"),
    NO_ITEMS_FOUND_EXCEPTION_MESSAGE("Service not found"),
    ACCEPT_ORDER_SUCCESS_MESSAGE("SUCCESS"),
    CUSTOMER_PROFILE_UPDATE_STATUS_MESSAGE("SUCCESS");

    private final    String message;

    StatusResponse(String message){
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

}
