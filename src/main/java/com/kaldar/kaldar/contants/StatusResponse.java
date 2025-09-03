package com.kaldar.kaldar.contants;

public enum StatusResponse {

    DRY_CLEANER_REGISTRATION_SUCCESS_MESSAGE("Registered drycleaner successfully"),
    VERIFICATION_TOKEN_SENT_MESSAGE("An otp has been sent to your email, please verify it."),
    CUSTOMER_REGISTRATION_SUCCESS_MESSAGE("Registered customer successfully");

    private final    String message;

    StatusResponse(String message){
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

}
