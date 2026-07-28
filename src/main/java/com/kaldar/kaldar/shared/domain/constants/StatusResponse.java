package com.kaldar.kaldar.shared.domain.constants;

public enum StatusResponse {

    DRY_CLEANER_REGISTRATION_SUCCESS_MESSAGE("Account created successfully! Please check your email for the verification code."),
    VERIFICATION_TOKEN_SENT_MESSAGE("A verification code has been sent to your email address."),
    CUSTOMER_REGISTRATION_SUCCESS_MESSAGE("Account registered successfully! Please check your email for your verification code."),
    CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE("We couldn't find a customer account matching those details."),
    OTP_NOT_FOUND_EXCEPTION_MESSAGE("No active verification code was found for this email address."),
    EXPIRED_OTP_EXCEPTION_MESSAGE("Your verification code has expired. Please request a new code."),
    INVALID_OTP_EXCEPTION("The verification code you entered is incorrect. Please check and try again."),
    VERIFICATION_OTP_SUCCESS_MESSAGE("Your account has been verified successfully!"),
    RESEND_VERIFICATION_OTP_SUCCESS_MESSAGE("A new verification code has been sent to your email."),
    USER_ALREADY_VERIFIED_MESSAGE("Your account is already verified. You can log in directly."),
    AUTHENTICATION_SUCCESS_MESSAGE("Logged in successfully."),
    ORDERS_NOT_FOUND_EXCEPTION_MESSAGE("No order found matching your request."),
    DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE("Dry cleaner store not found."),
    NO_ITEMS_FOUND_EXCEPTION_MESSAGE("Selected service items could not be found."),
    ACCEPT_ORDER_SUCCESS_MESSAGE("Order accepted successfully."),
    CUSTOMER_PROFILE_UPDATE_STATUS_MESSAGE("Profile updated successfully."),
    ORDER_CREATED_SUCCESS_MESSAGE("Order placed successfully."),
    DRY_CLEANER_PROFILE_UPDATED_SUCCESS_MESSAGE("Store profile updated successfully."),
    BUSINESS_VERIFICATION_SUBMITTED_MESSAGE("Business verification submitted successfully."),
    BUSINESS_VERIFICATION_SUCCESS_MESSAGE("Business verified successfully."),

    // Notification module
    NOTIFICATION_NOT_FOUND("Notification not found."),
    NOTIFICATION_DELETED("Notification deleted successfully."),
    NOTIFICATION_READ_UPDATED("Notification read status updated."),
    NOTIFICATIONS_BULK_UPDATED("Notifications updated successfully."),
    NOTIFICATIONS_ALL_READ("All notifications marked as read."),

    // Favourite module
    FAVOURITE_ADDED("Dry cleaner added to your favorites."),
    FAVOURITE_REMOVED("Dry cleaner removed from your favorites."),
    FAVOURITE_ALREADY_EXISTS("This dry cleaner is already in your favorites."),
    FAVOURITE_NOT_FOUND("Favorite entry not found."),

    // Wallet module
    WALLET_BALANCE_FETCHED("Wallet balance retrieved successfully."),

    // Payment module
    PAYMENT_INITIATED("Payment initiated successfully."),
    PAYMENT_WEBHOOK_RECEIVED("Webhook processed successfully."),
    PAYMENT_HISTORY_FETCHED("Payment history retrieved successfully.");

    private final String message;

    StatusResponse(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
