package com.kaldar.kaldar.shared.domain.constants;

public enum StatusResponse {

    DRY_CLEANER_REGISTRATION_SUCCESS_MESSAGE("Account created. Please verify your email with the OTP sent"),
    VERIFICATION_TOKEN_SENT_MESSAGE("An OTP was has been sent to your email. Please verify it"),
    CUSTOMER_REGISTRATION_SUCCESS_MESSAGE("Registered customer successfully"),
    CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE("Customer not found"),
    OTP_NOT_FOUND_EXCEPTION_MESSAGE("OTP not found for this user"),
    EXPIRED_OTP_EXCEPTION_MESSAGE("OTP has expired"),
    INVALID_OTP_EXCEPTION("Invalid Otp"),
    VERIFICATION_OTP_SUCCESS_MESSAGE("Account verified successfully"),
    RESEND_VERIFICATION_OTP_SUCCESS_MESSAGE("Successfully resent otp"),
    USER_ALREADY_VERIFIED_MESSAGE("User is already verified"),
    AUTHENTICATION_SUCCESS_MESSAGE("Login successfully"),
    ORDERS_NOT_FOUND_EXCEPTION_MESSAGE("Order not found "),
    DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE("Drycleaner not found"),
    NO_ITEMS_FOUND_EXCEPTION_MESSAGE("Service not found"),
    ACCEPT_ORDER_SUCCESS_MESSAGE("SUCCESS"),
    CUSTOMER_PROFILE_UPDATE_STATUS_MESSAGE("SUCCESS"),
    ORDER_CREATED_SUCCESS_MESSAGE("Order created"),
    DRY_CLEANER_PROFILE_UPDATED_SUCCESS_MESSAGE("DryCleaner profile updated"),
    BUSINESS_VERIFICATION_SUBMITTED_MESSAGE("Business verification submitted successfully"),
    BUSINESS_VERIFICATION_SUCCESS_MESSAGE("Business verified successfully"),

    // Notification module
    NOTIFICATION_NOT_FOUND("Notification not found"),
    NOTIFICATION_DELETED("Notification deleted successfully"),
    NOTIFICATION_READ_UPDATED("Notification read status updated"),
    NOTIFICATIONS_BULK_UPDATED("Notifications updated successfully"),
    NOTIFICATIONS_ALL_READ("All notifications marked as read"),

    // Favourite module
    FAVOURITE_ADDED("Dry cleaner added to favourites"),
    FAVOURITE_REMOVED("Dry cleaner removed from favourites"),
    FAVOURITE_ALREADY_EXISTS("Dry cleaner is already in your favourites"),
    FAVOURITE_NOT_FOUND("Favourite not found"),

    // Wallet module
    WALLET_BALANCE_FETCHED("Wallet balance retrieved"),

    // Payment module
    PAYMENT_INITIATED("Payment initiated successfully"),
    PAYMENT_WEBHOOK_RECEIVED("Webhook processed"),
    PAYMENT_HISTORY_FETCHED("Payment history retrieved");

    private final    String message;

    StatusResponse(String message){
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

}
