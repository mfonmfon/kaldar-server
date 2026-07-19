# Kaldar API Documentation

This document contains all the endpoints, request payloads, and success response payloads for all platform services in the Kaldar server. You can use these details to test the API on Postman.

### Base URL: `http://localhost:8050`

---

## Table of Contents
1. [Authentication & Verification Service](#1-authentication--verification-service)
2. [Customer Profile Service](#2-customer-profile-service)
3. [Dry Cleaner Profile & Service Catalog](#3-dry-cleaner-profile--service-catalog)
4. [Order Processing & Lifecycle Service](#4-order-processing--lifecycle-service)
5. [Review & Feedback Service](#5-review--feedback-service)
6. [Logistics & Webhook Service (Chowdeck Integration)](#6-logistics--webhook-service-chowdeck-integration)

---

## 1. Authentication & Verification Service

### Customer Registration
* **Method**: `POST`
* **Endpoint**: `/api/v1/customers/register`
* **Headers**: `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "customer@kaldar.com",
  "password": "Password123!",
  "phoneNumber": "+2348011111111",
  "address": "123 Laundry Lane, Lagos"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "email": "customer@kaldar.com",
  "expiresAt": "2026-07-20T10:15:00Z",
  "verificationMessage": "Verification code sent to email"
}
```

---

### Dry Cleaner Registration
* **Method**: `POST`
* **Endpoint**: `/api/v1/drycleaners/register`
* **Headers**: `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "firstName": "Sarah",
  "lastName": "Cleaner",
  "email": "sarah@sparkle.com",
  "businessEmail": "orders@sparkle.com",
  "businessName": "Sparkle Dry Cleaners",
  "shopAddress": "45 Commercial Ave, Lagos",
  "businessPhoneNumber": "+2349022222222",
  "password": "SecurePassword123!"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "email": "sarah@sparkle.com",
  "expiresAt": "2026-07-20T10:15:00Z",
  "verificationMessage": "Verification code sent to email"
}
```

---

### Verify OTP (Email Verification)
* **Method**: `POST`
* **Endpoint**: `/api/v1/auth/verify-otp`
* **Headers**: `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "email": "customer@kaldar.com",
  "otpInput": "123456"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "email": "customer@kaldar.com",
  "verifiedAt": "2026-07-20T10:05:00Z",
  "otpVerificationMessage": "OTP verification successful"
}
```

---

### Resend OTP
* **Method**: `POST`
* **Endpoint**: `/api/v1/auth/resend-otp`
* **Headers**: `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "email": "customer@kaldar.com"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "email": "customer@kaldar.com",
  "verifiedAt": "2026-07-20T10:20:00Z",
  "otpVerificationMessage": "Resend OTP code sent successfully"
}
```

---

### Login (Obtain JWT Token)
* **Method**: `POST`
* **Endpoint**: `/api/v1/auth/login`
* **Headers**: `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "email": "customer@kaldar.com",
  "password": "Password123!"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "email": "customer@kaldar.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["CUSTOMER"]
}
```

---

### Request Password Reset Link (Forgot Password)
* **Method**: `POST`
* **Endpoint**: `/api/v1/auth/forgot-password`
* **Headers**: `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "email": "customer@kaldar.com"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "email": "customer@kaldar.com",
  "message": "Password reset token sent",
  "expiresAt": "2026-07-20T10:20:00Z",
  "resetUrl": "http://localhost:8080/reset-password.html?email=customer%40kaldar.com&token=rawToken123"
}
```

---

### Reset Password
* **Method**: `POST`
* **Endpoint**: `/api/v1/auth/reset-password`
* **Headers**: `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "email": "customer@kaldar.com",
  "token": "rawToken123",
  "newPassword": "NewPassword123!"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "message": "Password reset successful",
  "confirmationUrl": "http://localhost:8080/reset-password-confirmation.html"
}
```

---

## 2. Customer Profile Service

All endpoints in this section require the customer JWT authentication token.

### Get Customer Profile
* **Method**: `GET`
* **Endpoint**: `/api/v1/customers/{id}/profile`
* **Headers**: `Authorization: Bearer <JWT_TOKEN>`
* **Success Response Body (200 OK)**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": null,
  "phoneNumber": "+2348011111111",
  "address": "123 Laundry Lane, Lagos"
}
```

---

### Update Customer Profile
* **Method**: `PUT`
* **Endpoint**: `/api/v1/customers/profile`
* **Headers**: 
  * `Authorization: Bearer <JWT_TOKEN>`
  * `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "customerId": 1,
  "firstName": "Johnathan",
  "lastName": "Doeland",
  "phoneNumber": "+2348011111111",
  "defaultAddress": "456 New Road, Abuja"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "firstName": "Johnathan",
  "lastName": "Doeland",
  "email": null,
  "phoneNumber": "+2348011111111",
  "address": "456 New Road, Abuja"
}
```

---

### Change Password
* **Method**: `POST`
* **Endpoint**: `/api/v1/customers/change-password`
* **Headers**: 
  * `Authorization: Bearer <JWT_TOKEN>`
  * `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "customerId": 1,
  "oldPassword": "Password123!",
  "newPassword": "NewPassword456!"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "statusCode": "SUCCESS",
  "responseMessage": "Password changed successfully"
}
```

---

## 3. Dry Cleaner Profile & Service Catalog

All endpoints in this section require the dry cleaner JWT authentication token.

### Edit Business Profile
* **Method**: `PUT`
* **Endpoint**: `/api/v1/drycleaners/profile`
* **Headers**: 
  * `Authorization: Bearer <JWT_TOKEN>`
  * `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "dryCleanerId": 2,
  "firstName": "Sarah",
  "lastName": "Johnson",
  "businessName": "Sparkle Cleaners Premium",
  "shopAddress": "90 Herbert Macaulay Way, Lagos",
  "businessPhoneNumber": "+2349022222222"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "message": "Drycleaner profile updated successfully"
}
```

---

### Update Payout Bank Details
* **Method**: `PATCH`
* **Endpoint**: `/api/v1/drycleaners/{id}/payout-account`
* **Headers**: 
  * `Authorization: Bearer <JWT_TOKEN>`
  * `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "accountName": "Sparkle Drycleaners Ltd",
  "accountNumber": "0123456789",
  "bankCode": "058",
  "bankName": "GTBank"
}
```
* **Success Response Body (200 OK)**: (Returns HTTP 200 with no content or basic success string)

---

### Update Operations Working Hours
* **Method**: `PATCH`
* **Endpoint**: `/api/v1/drycleaners/{id}/working-hours`
* **Headers**: 
  * `Authorization: Bearer <JWT_TOKEN>`
  * `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "workingHoursJson": "{\"monday\":{\"open\":\"08:00\",\"close\":\"18:00\"}}"
}
```
* **Success Response Body (200 OK)**: (Returns HTTP 200)

---

### Submit Business Verification (CAC Documents)
* **Method**: `POST`
* **Endpoint**: `/api/v1/drycleaners/{id}/verify-business`
* **Headers**: 
  * `Authorization: Bearer <JWT_TOKEN>`
  * `Content-Type: multipart/form-data`
* **Form-Data Params**:
  * `cacDocument` : File (PDF or Image)
  * `businessType` : `SOLE_PROPRIETORSHIP`
  * `registrationNumber` : `RC123456`
  * `taxIdentificationNumber` : `TIN123456`
* **Success Response Body (200 OK)**:
```json
{
  "businessName": "Sparkle Dry Cleaners",
  "verificationStatus": "PENDING"
}
```

---

### Fetch Onboarding Checklist Status
* **Method**: `GET`
* **Endpoint**: `/api/v1/drycleaners/{id}/onboarding-status`
* **Headers**: `Authorization: Bearer <JWT_TOKEN>`
* **Success Response Body (200 OK)**:
```json
{
  "businessVerified": true,
  "storeProfileSetup": true,
  "payoutAccountAdded": true,
  "businessOperationsSetup": true,
  "storeInventorySetup": true
}
```

---

### Fetch Business Analytics Dashboard
* **Method**: `GET`
* **Endpoint**: `/api/v1/drycleaners/{id}/analytics?period=month`
* **Headers**: `Authorization: Bearer <JWT_TOKEN>`
* **Success Response Body (200 OK)**:
```json
{
  "revenue": 145000.0,
  "revenuePercentageChange": "+12.5%",
  "orders": 12,
  "ordersPercentageChange": "+5",
  "avgRating": 4.5,
  "ratingPercentageChange": "+0.2"
}
```

---

### List Catalog Service Offerings
* **Method**: `GET`
* **Endpoint**: `/api/v1/drycleaners/{id}/services`
* **Headers**: `Authorization: Bearer <JWT_TOKEN>`
* **Success Response Body (200 OK)**:
```json
[
  {
    "id": 10,
    "serviceName": "Suit Jacket Dry Cleaning",
    "unitPrice": 3500.0,
    "description": "Premium dry cleaning for suit jackets"
  }
]
```

---

### Add or Update Service Offering
* **Method**: `POST`
* **Endpoint**: `/api/v1/drycleaners/{id}/services`
* **Headers**: 
  * `Authorization: Bearer <JWT_TOKEN>`
  * `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "serviceName": "Suit Jacket Dry Cleaning",
  "unitPrice": 3500.0,
  "description": "Premium dry cleaning for suit jackets"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "id": 10,
  "serviceName": "Suit Jacket Dry Cleaning",
  "unitPrice": 3500.0,
  "description": "Premium dry cleaning for suit jackets"
}
```

---

## 4. Order Processing & Lifecycle Service

### Place a New Order
* **Method**: `POST`
* **Endpoint**: `/api/v1/orders`
* **Headers**: 
  * `Authorization: Bearer <JWT_TOKEN>`
  * `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "customerId": 1,
  "dryCleanerId": 2,
  "pickupAddress": "123 Laundry Lane, Lagos",
  "deliveryAddress": "123 Laundry Lane, Lagos",
  "washingPreference": "No starch, soft detergent",
  "serviceItems": [
    {
      "serviceOfferingId": 10,
      "quantity": 3
    }
  ]
}
```
* **Success Response Body (200 OK)**:
```json
{
  "orderId": 50,
  "customerId": 1,
  "dryCleanerId": 2,
  "pickupAddress": "123 Laundry Lane, Lagos",
  "deliveryAddress": "123 Laundry Lane, Lagos",
  "totalPrice": 10500.0,
  "createdAt": "2026-07-20T10:00:00Z",
  "status": "Order created successfully"
}
```

---

### Accept Order (Triggers Chowdeck Dispatch Pickup Rider)
* **Method**: `POST`
* **Endpoint**: `/api/v1/orders/accept`
* **Headers**: 
  * `Authorization: Bearer <JWT_TOKEN>`
  * `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "orderId": 50,
  "dryCleanerId": 2,
  "pickupAt": "2026-07-20T10:00:00"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "orderId": 50,
  "status": "ACCEPTED",
  "timestamp": "2026-07-20T10:05:00Z"
}
```

---

### Reject Order
* **Method**: `POST`
* **Endpoint**: `/api/v1/orders/reject`
* **Headers**: 
  * `Authorization: Bearer <JWT_TOKEN>`
  * `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "orderId": 50,
  "dryCleanerId": 2,
  "reason": "Shop at full laundry capacity"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "orderId": 50,
  "status": "REJECTED",
  "timestamp": "2026-07-20T10:05:00Z"
}
```

---

### Get Order Details By ID
* **Method**: `GET`
* **Endpoint**: `/api/v1/orders/{id}`
* **Headers**: `Authorization: Bearer <JWT_TOKEN>`
* **Success Response Body (200 OK)**:
```json
{
  "id": 50,
  "orderNumber": "ORD-50",
  "status": "accepted",
  "createdAt": "2026-07-20T10:00:00Z",
  "pickupAddress": "123 Laundry Lane, Lagos",
  "deliveryAddress": "123 Laundry Lane, Lagos",
  "pickupTime": "2026-07-20T10:00:00Z",
  "deliveryTime": null,
  "totalCost": 10500.0,
  "cleanerName": "Sparkle Dry Cleaners",
  "customerName": "John Doe",
  "items": [
    {
      "clothType": "Suit Jacket Dry Cleaning",
      "quantity": 3,
      "pricePerItem": 3500.0,
      "subtotal": 10500.0
    }
  ]
}
```

---

### Get Orders List by Customer ID
* **Method**: `GET`
* **Endpoint**: `/api/v1/orders/customer/{customerId}`
* **Headers**: `Authorization: Bearer <JWT_TOKEN>`
* **Success Response Body (200 OK)**: (Returns a JSON array of order details objects shown above)

---

### Get Orders List by Dry Cleaner ID
* **Method**: `GET`
* **Endpoint**: `/api/v1/orders/cleaner/{dryCleanerId}`
* **Headers**: `Authorization: Bearer <JWT_TOKEN>`
* **Success Response Body (200 OK)**: (Returns a JSON array of order details objects shown above)

---

### Update Order Status Leg
* **Method**: `PATCH`
* **Endpoint**: `/api/v1/orders/status`
* **Headers**: 
  * `Authorization: Bearer <JWT_TOKEN>`
  * `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "orderId": 50,
  "status": "READY"
}
```
* **Success Response Body (200 OK)**:
```json
{
  "orderId": 50,
  "status": "READY",
  "updatedAt": "2026-07-20T12:00:00Z"
}
```

---

## 5. Review & Feedback Service

### Submit Order Review
* **Method**: `POST`
* **Endpoint**: `/api/v1/reviews`
* **Headers**: 
  * `Authorization: Bearer <JWT_TOKEN>`
  * `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "orderId": 50,
  "customerId": 1,
  "dryCleanerId": 2,
  "rating": 5,
  "comment": "Excellent cleaning! The clothes were spotless."
}
```
* **Success Response Body (200 OK)**:
```json
{
  "id": 1,
  "orderId": 50,
  "rating": 5,
  "comment": "Excellent cleaning! The clothes were spotless.",
  "createdAt": "2026-07-20T13:00:00Z",
  "customerName": "John Doe"
}
```

---

### Fetch Reviews for a Dry Cleaner
* **Method**: `GET`
* **Endpoint**: `/api/v1/reviews/cleaner/{dryCleanerId}`
* **Headers**: `Authorization: Bearer <JWT_TOKEN>`
* **Success Response Body (200 OK)**: (Returns a JSON array of review objects shown above)

---

## 6. Logistics & Webhook Service (Chowdeck Integration)

### Receive Dispatch Webhook Callback
* **Method**: `POST`
* **Endpoint**: `/api/v1/webhooks/logistics/{provider}`
  * *Example*: `/api/v1/webhooks/logistics/chowdeck`
* **Headers**: `Content-Type: application/json`
* **Request Body (Raw JSON)**:
```json
{
  "category": "PICKED_UP",
  "description": "Rider has collected the laundry clothes from the customer",
  "payload": {
    "id": "3122",
    "reference": "uxj6ztfjt2sstxwiap69",
    "status": "preparing"
  }
}
```
* **Success Response Body (200 OK)**:
```json
{
  "status": "success",
  "message": "Webhook received successfully"
}
```

---

## 7. Wallet Ledger Service

### Get Wallet Summary
* **Method**: `GET`
* **Endpoint**: `/api/v1/wallets/{userId}/summary`
* **Headers**: `Authorization: Bearer <JWT_TOKEN>`
* **Success Response Body (200 OK)**:
```json
{
  "balance": 8500.00,
  "transactionHistory": [
    {
      "id": 1,
      "amount": 8500.00,
      "type": "CREDIT",
      "description": "Earnings for Order #50",
      "reference": "ORD-50",
      "createdAt": "2026-07-20T10:15:00Z"
    }
  ]
}
```

