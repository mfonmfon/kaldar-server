# Kaldar API Specification

## Overview
Complete REST API documentation for the Kaldar Dry Cleaning Marketplace platform.

**Base URL**: `http://localhost:8080`  
**API Version**: v1  
**Authentication**: JWT Bearer Token (except public endpoints)

---

## Response Format

All API responses follow this standard structure:

```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Success message",
  "data": { /* response payload */ }
}
```

**Error Response**:
```json
{
  "isSuccess": false,
  "status": 400,
  "message": "Error message",
  "errors": ["Validation error 1", "Validation error 2"]
}
```

---

## 1. Authentication APIs

### 1.1 Login
**Endpoint**: `POST /api/v1/auth/login`  
**Access**: Public  
**Description**: Authenticate user and receive JWT token

**Request Body**:
```json
{
  "email": "customer@example.com",
  "password": "SecurePass123!"
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Authentication successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "email": "customer@example.com",
    "role": "CUSTOMER"
  }
}
```

---

### 1.2 Forgot Password
**Endpoint**: `POST /api/v1/auth/forgot-password`  
**Access**: Public  
**Description**: Request password reset token

**Request Body**:
```json
{
  "email": "customer@example.com"
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Password reset token sent",
  "data": {
    "message": "Password reset instructions sent to email",
    "email": "customer@example.com"
  }
}
```

---

### 1.3 Reset Password
**Endpoint**: `POST /api/v1/auth/reset-password`  
**Access**: Public  
**Description**: Reset password using token from email

**Request Body**:
```json
{
  "token": "abc123def456",
  "newPassword": "NewSecurePass123!"
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Password reset successful",
  "data": {
    "message": "Password has been reset successfully"
  }
}
```

---

## 2. Customer APIs

### 2.1 Register Customer
**Endpoint**: `POST /api/v1/auth/register`  
**Access**: Public  
**Description**: Register new customer account

**Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "phoneNumber": "+2348012345678",
  "address": "123 Main Street, Lagos"
}
```

**Response** (201 Created):
```json
{
  "isSuccess": true,
  "status": 201,
  "message": "Customer registration successful",
  "data": {
    "customerId": 1,
    "email": "john.doe@example.com",
    "message": "Verification email sent"
  }
}
```

---

### 2.2 Get Customer Profile
**Endpoint**: `GET /api/v1/auth/{customerId}`  
**Access**: Authenticated (Customer, Admin)  
**Description**: Retrieve customer profile details

**Path Parameters**:
- `customerId` (Long): Customer ID

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Customer profile retrieved",
  "data": {
    "customerId": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+2348012345678",
    "address": "123 Main Street, Lagos",
    "createdAt": "2026-03-20T10:30:00"
  }
}
```

---

### 2.3 Update Customer Profile
**Endpoint**: `PUT /api/v1/auth/updated_customer_profile`  
**Access**: Authenticated (Customer)  
**Description**: Update customer profile information     },
      {
        "clothType": "TROUSERS",
        "quantity": 2,
        "pricePerItem": 2000.00,
        "subtotal": 4000.00
      }
    ],
    "totalPrice": 11500.00,
    "currency": "NGN",
    "estimatedTurnaroundHours": 48
  }
}
```

---

## 5. Order APIs

### 5.1 Create Order
**Endpoint**: `POST /api/v1/order/create-order`  
**Access**: Authenticated (Customer)  
**Description**: Create new order with selected dry cleaner

**Request Body**:
othTypes` (String[], required): Cloth types
- `quantities` (Integer[], required): Quantities

**Example Request**:
```
GET /api/v1/drycleaners/1/quote?clothTypes=SHIRT,TROUSERS&quantities=5,2
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Quote calculated",
  "data": {
    "dryCleanerId": 1,
    "businessName": "Sparkle Cleaners",
    "items": [
      {
        "clothType": "SHIRT",
        "quantity": 5,
        "pricePerItem": 1500.00,
        "subtotal": 7500.00
 "Mon-Sat: 8AM-6PM",
        "rating": 4.5,
        "isActive": true
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 5,
    "totalPages": 1
  }
}
```

---

### 4.2 Get Price Quote (To Be Implemented)
**Endpoint**: `GET /api/v1/drycleaners/{dryCleanerId}/quote`  
**Access**: Authenticated (Customer)  
**Description**: Get detailed price quote from specific dry cleaner

**Path Parameters**:
- `dryCleanerId` (Long): Dry cleaner ID

**Query Parameters**:
- `clude": 6.5244,
        "longitude": 3.3792,
        "totalPrice": 8000.00,
        "currency": "NGN",
        "serviceBreakdown": [
          {
            "clothType": "SHIRT",
            "quantity": 3,
            "pricePerItem": 1500.00,
            "subtotal": 4500.00
          },
          {
            "clothType": "SUIT",
            "quantity": 1,
            "pricePerItem": 3500.00,
            "subtotal": 3500.00
          }
        ],
        "estimatedTurnaroundHours": 48,
        "workingHours": ze` (Integer, optional): Page size (default: 20)

**Example Request**:
```
GET /api/v1/drycleaners?latitude=6.5244&longitude=3.3792&radiusKm=10&clothTypes=SHIRT,SUIT&quantities=3,1&page=0&size=10
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Drycleaners fetched",
  "data": {
    "content": [
      {
        "dryCleanerId": 1,
        "businessName": "Sparkle Cleaners",
        "businessAddress": "45 Commercial Avenue, Lagos",
        "distanceKm": 2.5,
        "latit  
**Access**: Authenticated (Customer)  
**Description**: Search for nearby dry cleaners with price quotes

**Query Parameters**:
- `latitude` (Double, required): Customer latitude
- `longitude` (Double, required): Customer longitude
- `radiusKm` (Double, optional): Search radius in kilometers (default: 10km)
- `clothTypes` (String[], optional): Cloth types needed (e.g., SHIRT, SUIT)
- `quantities` (Integer[], optional): Quantities for each cloth type
- `page` (Integer, optional): Page number (default: 0)
- `sicleaner

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Services retrieved",
  "data": [
    {
      "serviceId": 1,
      "clothType": "SHIRT",
      "price": 1500.00,
      "currency": "NGN",
      "turnaroundHours": 48
    },
    {
      "serviceId": 2,
      "clothType": "SUIT",
      "price": 3500.00,
      "currency": "NGN",
      "turnaroundHours": 72
    }
  ]
}
```

---

## 4. Discovery APIs

### 4.1 Find Available Dry Cleaners
**Endpoint**: `GET /api/v1/drycleaners`
  "expressPrice": 2500.00,
  "expressTurnaroundHours": 24
}
```

**Response** (201 Created):
```json
{
  "isSuccess": true,
  "status": 201,
  "message": "Service offering added",
  "data": {
    "serviceId": 1,
    "clothType": "SHIRT",
    "price": 1500.00,
    "currency": "NGN",
    "turnaroundHours": 48
  }
}
```

---

### 3.4 Get Dry Cleaner Services (To Be Implemented)
**Endpoint**: `GET /api/v1/drycleaner/{dryCleanerId}/services`  
**Access**: Public  
**Description**: Get all service offerings for a dry  6.5244,
    "longitude": 3.3792,
    "workingHours": "Mon-Sun: 7AM-8PM",
    "description": "Premium dry cleaning with express service"
  }
}
```

---

### 3.3 Manage Service Offerings (To Be Implemented)
**Endpoint**: `POST /api/v1/drycleaner/{dryCleanerId}/services`  
**Access**: Authenticated (Dry Cleaner)  
**Description**: Add or update service offerings and pricing

**Request Body**:
```json
{
  "clothType": "SHIRT",
  "price": 1500.00,
  "currency": "NGN",
  "turnaroundHours": 48,
  "expressAvailable": true,
  "businessAddress": "45 Commercial Avenue, Lagos",
  "workingHours": "Mon-Sun: 7AM-8PM",
  "description": "Premium dry cleaning with express service"
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Dry cleaner profile updated successfully",
  "data": {
    "dryCleanerId": 1,
    "businessName": "Sparkle Premium Cleaners",
    "email": "contact@sparklecleaners.com",
    "phoneNumber": "+2348098765432",
    "businessAddress": "45 Commercial Avenue, Lagos",
    "latitude":{
  "isSuccess": true,
  "status": 201,
  "message": "Dry cleaner registration successful",
  "data": {
    "message": "Verification email sent to contact@sparklecleaners.com"
  }
}
```

---

### 3.2 Update Dry Cleaner Profile
**Endpoint**: `PATCH /api/v1/auth/drycleaner/edit-profile`  
**Access**: Authenticated (Dry Cleaner)  
**Description**: Update dry cleaner business profile

**Request Body**:
```json
{
  "dryCleanerId": 1,
  "businessName": "Sparkle Premium Cleaners",
  "phoneNumber": "+2348098765432",ST /api/v1/auth/drycleaner/register`  
**Access**: Public  
**Description**: Register new dry cleaner business

**Request Body**:
```json
{
  "businessName": "Sparkle Cleaners",
  "email": "contact@sparklecleaners.com",
  "password": "SecurePass123!",
  "phoneNumber": "+2348098765432",
  "businessAddress": "45 Commercial Avenue, Lagos",
  "latitude": 6.5244,
  "longitude": 3.3792,
  "workingHours": "Mon-Sat: 8AM-6PM",
  "description": "Premium dry cleaning services"
}
```

**Response** (201 Created):
```json
 Change Password
**Endpoint**: `PUT /api/v1/auth/change_password`  
**Access**: Authenticated (Customer)  
**Description**: Change customer password

**Request Body**:
```json
{
  "customerId": 1,
  "oldPassword": "OldPass123!",
  "newPassword": "NewSecurePass123!"
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "SUCCESS",
  "data": {
    "message": "Password changed successfully"
  }
}
```

---

## 3. Dry Cleaner APIs

### 3.1 Register Dry Cleaner
**Endpoint**: `PO

**Request Body**:
```json
{
  "customerId": 1,
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+2348012345678",
  "address": "456 New Street, Lagos"
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Customer profile updated successfully",
  "data": {
    "customerId": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+2348012345678",
    "address": "456 New Street, Lagos"
  }
}
```

---

### 2.4
```json
{
  "customerId": 1,
  "dryCleanerId": 1,
  "pickupTime": "2026-03-28T10:00:00",
  "deliveryTime": "2026-03-30T16:00:00",
  "pickupAddress": "123 Main Street, Lagos",
  "deliveryAddress": "123 Main Street, Lagos",
  "items": [
    {
      "clothType": "SHIRT",
      "quantity": 3,
      "specialInstructions": "Handle with care"
    },
    {
      "clothType": "SUIT",
      "quantity": 1,
      "specialInstructions": "Stain on left sleeve"
    }
  ]
}
```

**Response** (201 Created):
```json
{
  "isSuccess": true,
  "status": 201,
  "message": "Order created successfully",
  "data": {
    "orderId": 1,
    "orderNumber": "ORD-20260328-001",
    "customerId": 1,
    "dryCleanerId": 1,
    "orderStatus": "PENDING_ACCEPTANCE",
    "totalPrice": 8000.00,
    "currency": "NGN",
    "pickupTime": "2026-03-28T10:00:00",
    "deliveryTime": "2026-03-30T16:00:00",
    "createdAt": "2026-03-27T14:30:00"
  }
}
```

---

### 5.2 Accept Order
**Endpoint**: `POST /api/v1/order`  
**Access**: Authenticated (Dry Cleaner)  
**Description**: Dry cleaner accepts order

**Request Body**:
```json
{
  "orderId": 1,
  "dryCleanerId": 1
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Order accepted successfully",
  "data": {
    "orderId": 1,
    "orderStatus": "ACCEPTED",
    "acceptedAt": "2026-03-27T15:00:00"
  }
}
```

---

### 5.3 Update Order Status
**Endpoint**: `PATCH /api/v1/order/{orderId}/status`  
**Access**: Authenticated (Dry Cleaner, Rider, Admin)  
**Description**: Update order status through state machine

**Path Parameters**:
- `orderId` (Long): Order ID

**Request Body**:
```json
{
  "newStatus": "PICKED_UP",
  "notes": "Items picked up by rider"
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Order status updated",
  "data": {
    "orderId": 1,
    "previousStatus": "SCHEDULED",
    "currentStatus": "PICKED_UP",
    "updatedAt": "2026-03-28T10:15:00"
  }
}
```

**Valid Status Transitions**:
- PENDING_ACCEPTANCE → ACCEPTED, REJECTED
- ACCEPTED → SCHEDULED
- SCHEDULED → PICKED_UP, CANCELLED
- PICKED_UP → CLEANING
- CLEANING → READY_FOR_DELIVERY
- READY_FOR_DELIVERY → OUT_FOR_DELIVERY
- OUT_FOR_DELIVERY → DELIVERED
- DELIVERED → COMPLETED

---

### 5.4 Get Order Details (To Be Implemented)
**Endpoint**: `GET /api/v1/order/{orderId}`  
**Access**: Authenticated (Customer, Dry Cleaner, Rider, Admin)  
**Description**: Get complete order details

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Order retrieved",
  "data": {
    "orderId": 1,
    "orderNumber": "ORD-20260328-001",
    "customer": {
      "customerId": 1,
      "name": "John Doe",
      "phoneNumber": "+2348012345678"
    },
    "dryCleaner": {
      "dryCleanerId": 1,
      "businessName": "Sparkle Cleaners",
      "phoneNumber": "+2348098765432"
    },
    "items": [
      {
        "itemId": 1,
        "clothType": "SHIRT",
        "quantity": 3,
        "pricePerItem": 1500.00,
        "subtotal": 4500.00
      }
    ],
    "orderStatus": "PICKED_UP",
    "totalPrice": 8000.00,
    "pickupTime": "2026-03-28T10:00:00",
    "deliveryTime": "2026-03-30T16:00:00",
    "pickupAddress": "123 Main Street, Lagos",
    "deliveryAddress": "123 Main Street, Lagos",
    "createdAt": "2026-03-27T14:30:00",
    "statusHistory": [
      {
        "status": "PENDING_ACCEPTANCE",
        "timestamp": "2026-03-27T14:30:00"
      },
      {
        "status": "ACCEPTED",
        "timestamp": "2026-03-27T15:00:00"
      }
    ]
  }
}
```

---

### 5.5 Get Customer Orders (To Be Implemented)
**Endpoint**: `GET /api/v1/order/customer/{customerId}`  
**Access**: Authenticated (Customer, Admin)  
**Description**: Get all orders for a customer

**Query Parameters**:
- `status` (String, optional): Filter by order status
- `page` (Integer, optional): Page number
- `size` (Integer, optional): Page size

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Orders retrieved",
  "data": {
    "content": [
      {
        "orderId": 1,
        "orderNumber": "ORD-20260328-001",
        "dryCleanerName": "Sparkle Cleaners",
        "orderStatus": "PICKED_UP",
        "totalPrice": 8000.00,
        "createdAt": "2026-03-27T14:30:00"
      }
    ],
    "totalElements": 10,
    "totalPages": 1
  }
}
```

---

### 5.6 Get Dry Cleaner Orders (To Be Implemented)
**Endpoint**: `GET /api/v1/order/drycleaner/{dryCleanerId}`  
**Access**: Authenticated (Dry Cleaner, Admin)  
**Description**: Get all orders for a dry cleaner

**Query Parameters**:
- `status` (String, optional): Filter by order status
- `page` (Integer, optional): Page number
- `size` (Integer, optional): Page size

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Orders retrieved",
  "data": {
    "content": [
      {
        "orderId": 1,
        "orderNumber": "ORD-20260328-001",
        "customerName": "John Doe",
        "orderStatus": "PICKED_UP",
        "totalPrice": 8000.00,
        "pickupTime": "2026-03-28T10:00:00",
        "createdAt": "2026-03-27T14:30:00"
      }
    ],
    "totalElements": 25,
    "totalPages": 3
  }
}
```

---

### 5.7 Cancel Order (To Be Implemented)
**Endpoint**: `DELETE /api/v1/order/{orderId}`  
**Access**: Authenticated (Customer, Admin)  
**Description**: Cancel order (only before PICKED_UP status)

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Order cancelled",
  "data": {
    "orderId": 1,
    "orderStatus": "CANCELLED",
    "cancelledAt": "2026-03-27T16:00:00"
  }
}
```

---

## 6. Dispatch/Rider APIs (To Be Implemented)

### 6.1 Register Rider
**Endpoint**: `POST /api/v1/dispatch/riders/register`  
**Access**: Public  
**Description**: Register new dispatch rider

**Request Body**:
```json
{
  "firstName": "Michael",
  "lastName": "Johnson",
  "email": "michael.rider@example.com",
  "password": "SecurePass123!",
  "phoneNumber": "+2348087654321",
  "vehicleType": "MOTORCYCLE",
  "vehicleNumber": "ABC-123-XY",
  "licenseNumber": "DL123456"
}
```

**Response** (201 Created):
```json
{
  "isSuccess": true,
  "status": 201,
  "message": "Rider registration successful",
  "data": {
    "riderId": 1,
    "email": "michael.rider@example.com",
    "message": "Verification email sent"
  }
}
```

---

### 6.2 Update Rider Availability
**Endpoint**: `PATCH /api/v1/dispatch/riders/{riderId}/availability`  
**Access**: Authenticated (Rider)  
**Description**: Update rider online/offline status

**Request Body**:
```json
{
  "available": true
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Availability updated",
  "data": {
    "riderId": 1,
    "available": true,
    "updatedAt": "2026-03-27T14:30:00"
  }
}
```

---

### 6.3 Update Rider Location
**Endpoint**: `PATCH /api/v1/dispatch/riders/{riderId}/location`  
**Access**: Authenticated (Rider)  
**Description**: Update rider current location (for tracking)

**Request Body**:
```json
{
  "latitude": 6.5244,
  "longitude": 3.3792
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Location updated",
  "data": {
    "riderId": 1,
    "latitude": 6.5244,
    "longitude": 3.3792,
    "updatedAt": "2026-03-27T14:35:00"
  }
}
```

---

### 6.4 Get Rider Tasks
**Endpoint**: `GET /api/v1/dispatch/riders/{riderId}/tasks`  
**Access**: Authenticated (Rider)  
**Description**: Get assigned delivery tasks

**Query Parameters**:
- `status` (String, optional): Filter by task status (ASSIGNED, IN_TRANSIT, COMPLETED)

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Tasks retrieved",
  "data": [
    {
      "taskId": 1,
      "orderId": 1,
      "orderNumber": "ORD-20260328-001",
      "taskType": "PICKUP",
      "status": "ASSIGNED",
      "pickupLocation": {
        "address": "123 Main Street, Lagos",
        "latitude": 6.5244,
        "longitude": 3.3792
      },
      "dropoffLocation": {
        "address": "45 Commercial Avenue, Lagos",
        "latitude": 6.5300,
        "longitude": 3.3800
      },
      "distanceKm": 1.2,
      "assignedAt": "2026-03-27T15:00:00",
      "scheduledTime": "2026-03-28T10:00:00"
    }
  ]
}
```

---

### 6.5 Complete Delivery Task
**Endpoint**: `PATCH /api/v1/dispatch/tasks/{taskId}/complete`  
**Access**: Authenticated (Rider)  
**Description**: Mark delivery task as completed

**Request Body**:
```json
{
  "completionNotes": "Delivered successfully",
  "completedAt": "2026-03-28T10:30:00"
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Task completed",
  "data": {
    "taskId": 1,
    "status": "COMPLETED",
    "completedAt": "2026-03-28T10:30:00"
  }
}
```

---

## 7. Verification APIs (To Be Implemented)

### 7.1 Verify Email
**Endpoint**: `POST /api/v1/auth/verify-email`  
**Access**: Public  
**Description**: Verify email using OTP token

**Request Body**:
```json
{
  "email": "customer@example.com",
  "otp": "123456"
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Email verified successfully",
  "data": {
    "verified": true,
    "email": "customer@example.com"
  }
}
```

---

### 7.2 Resend Verification Email
**Endpoint**: `POST /api/v1/auth/resend-verification`  
**Access**: Public  
**Description**: Resend verification OTP

**Request Body**:
```json
{
  "email": "customer@example.com"
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Verification email sent",
  "data": {
    "message": "New OTP sent to email"
  }
}
```

---

## 8. Admin APIs (To Be Implemented)

### 8.1 Get All Users
**Endpoint**: `GET /api/v1/admin/users`  
**Access**: Authenticated (Admin)  
**Description**: Get all users with filtering

**Query Parameters**:
- `role` (String, optional): Filter by role (CUSTOMER, DRY_CLEANER, RIDER)
- `verified` (Boolean, optional): Filter by verification status
- `page` (Integer, optional): Page number
- `size` (Integer, optional): Page size

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Users retrieved",
  "data": {
    "content": [
      {
        "userId": 1,
        "email": "customer@example.com",
        "role": "CUSTOMER",
        "verified": true,
        "createdAt": "2026-03-20T10:00:00"
      }
    ],
    "totalElements": 100,
    "totalPages": 10
  }
}
```

---

### 8.2 Get All Orders
**Endpoint**: `GET /api/v1/admin/orders`  
**Access**: Authenticated (Admin)  
**Description**: Get all orders with filtering

**Query Parameters**:
- `status` (String, optional): Filter by status
- `customerId` (Long, optional): Filter by customer
- `dryCleanerId` (Long, optional): Filter by dry cleaner
- `fromDate` (String, optional): Filter from date (ISO format)
- `toDate` (String, optional): Filter to date (ISO format)
- `page` (Integer, optional): Page number
- `size` (Integer, optional): Page size

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Orders retrieved",
  "data": {
    "content": [
      {
        "orderId": 1,
        "orderNumber": "ORD-20260328-001",
        "customerName": "John Doe",
        "dryCleanerName": "Sparkle Cleaners",
        "orderStatus": "PICKED_UP",
        "totalPrice": 8000.00,
        "createdAt": "2026-03-27T14:30:00"
      }
    ],
    "totalElements": 500,
    "totalPages": 50
  }
}
```

---

### 8.3 Suspend User
**Endpoint**: `PATCH /api/v1/admin/users/{userId}/suspend`  
**Access**: Authenticated (Admin)  
**Description**: Suspend user account

**Request Body**:
```json
{
  "reason": "Violation of terms of service",
  "suspendedUntil": "2026-04-27T00:00:00"
}
```

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "User suspended",
  "data": {
    "userId": 1,
    "suspended": true,
    "suspendedUntil": "2026-04-27T00:00:00"
  }
}
```

---

### 8.4 Get Analytics (To Be Implemented)
**Endpoint**: `GET /api/v1/admin/analytics`  
**Access**: Authenticated (Admin)  
**Description**: Get system analytics

**Query Parameters**:
- `fromDate` (String, required): Start date
- `toDate` (String, required): End date
- `metric` (String, optional): Specific metric (orders, revenue, users)

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Analytics retrieved",
  "data": {
    "period": {
      "from": "2026-03-01T00:00:00",
      "to": "2026-03-27T23:59:59"
    },
    "totalOrders": 500,
    "completedOrders": 450,
    "cancelledOrders": 30,
    "totalRevenue": 4000000.00,
    "newCustomers": 120,
    "newDryCleaners": 15,
    "activeRiders": 50,
    "averageOrderValue": 8000.00,
    "averageDeliveryTime": 48.5
  }
}
```

---

## 9. Notification APIs (To Be Implemented)

### 9.1 Get Notification History
**Endpoint**: `GET /api/v1/notifications`  
**Access**: Authenticated (All roles)  
**Description**: Get user notification history

**Query Parameters**:
- `page` (Integer, optional): Page number
- `size` (Integer, optional): Page size

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Notifications retrieved",
  "data": {
    "content": [
      {
        "notificationId": 1,
        "type": "ORDER_STATUS_UPDATE",
        "title": "Order Status Updated",
        "message": "Your order ORD-20260328-001 has been picked up",
        "read": false,
        "createdAt": "2026-03-28T10:15:00"
      }
    ],
    "unreadCount": 5,
    "totalElements": 20
  }
}
```

---

### 9.2 Mark Notification as Read
**Endpoint**: `PATCH /api/v1/notifications/{notificationId}/read`  
**Access**: Authenticated (All roles)  
**Description**: Mark notification as read

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Notification marked as read",
  "data": {
    "notificationId": 1,
    "read": true
  }
}
```

---

## 10. Rating & Review APIs (To Be Implemented)

### 10.1 Submit Review
**Endpoint**: `POST /api/v1/reviews`  
**Access**: Authenticated (Customer)  
**Description**: Submit review for completed order

**Request Body**:
```json
{
  "orderId": 1,
  "dryCleanerId": 1,
  "rating": 5,
  "comment": "Excellent service! Clothes came back spotless."
}
```

**Response** (201 Created):
```json
{
  "isSuccess": true,
  "status": 201,
  "message": "Review submitted",
  "data": {
    "reviewId": 1,
    "orderId": 1,
    "rating": 5,
    "createdAt": "2026-03-30T18:00:00"
  }
}
```

---

### 10.2 Get Dry Cleaner Reviews
**Endpoint**: `GET /api/v1/drycleaners/{dryCleanerId}/reviews`  
**Access**: Public  
**Description**: Get all reviews for a dry cleaner

**Query Parameters**:
- `page` (Integer, optional): Page number
- `size` (Integer, optional): Page size

**Response** (200 OK):
```json
{
  "isSuccess": true,
  "status": 200,
  "message": "Reviews retrieved",
  "data": {
    "dryCleanerId": 1,
    "averageRating": 4.5,
    "totalReviews": 150,
    "reviews": [
      {
        "reviewId": 1,
        "customerName": "John D.",
        "rating": 5,
        "comment": "Excellent service!",
        "createdAt": "2026-03-30T18:00:00"
      }
    ]
  }
}
```

---

## Authentication

### JWT Token
All authenticated endpoints require a JWT token in the Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Payload
```json
{
  "sub": "customer@example.com",
  "userId": 1,
  "role": "CUSTOMER",
  "iat": 1711545600,
  "exp": 1711632000
}
```

---

## Error Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request (validation error) |
| 401 | Unauthorized (missing/invalid token) |
| 403 | Forbidden (insufficient permissions) |
| 404 | Not Found |
| 409 | Conflict (duplicate resource) |
| 500 | Internal Server Error |

---

## Common Error Responses

### Validation Error (400)
```json
{
  "isSuccess": false,
  "status": 400,
  "message": "Validation failed",
  "errors": [
    "Email is required",
    "Password must be at least 8 characters"
  ]
}
```

### Unauthorized (401)
```json
{
  "isSuccess": false,
  "status": 401,
  "message": "Authentication required",
  "errors": ["Invalid or expired token"]
}
```

### Forbidden (403)
```json
{
  "isSuccess": false,
  "status": 403,
  "message": "Access denied",
  "errors": ["Insufficient permissions"]
}
```

### Not Found (404)
```json
{
  "isSuccess": false,
  "status": 404,
  "message": "Resource not found",
  "errors": ["Order with ID 999 not found"]
}
```

---

## Rate Limiting (To Be Implemented)

| Endpoint Category | Rate Limit |
|-------------------|------------|
| Authentication | 5 requests/minute |
| Registration | 3 requests/hour |
| Search/Discovery | 60 requests/minute |
| Order Operations | 30 requests/minute |
| Profile Updates | 10 requests/minute |

---

## Pagination

All list endpoints support pagination with these query parameters:
- `page`: Page number (0-indexed, default: 0)
- `size`: Items per page (default: 20, max: 100)
- `sort`: Sort field and direction (e.g., `createdAt,desc`)

**Paginated Response Structure**:
```json
{
  "content": [ /* items */ ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

---

## API Testing

### Using cURL

**Login**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"customer@example.com","password":"SecurePass123!"}'
```

**Create Order** (with token):
```bash
curl -X POST http://localhost:8080/api/v1/order/create-order \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"customerId":1,"dryCleanerId":1,"pickupTime":"2026-03-28T10:00:00",...}'
```

---

## Implementation Status

### ✅ Implemented
- Authentication (login, forgot password, reset password)
- Customer registration and profile management
- Dry cleaner registration and profile management
- Order creation, acceptance, status updates
- Basic discovery (list dry cleaners)

### ⏳ To Be Implemented
- Geospatial search with distance calculation
- Price quote calculation
- Rider registration and management
- Automatic rider assignment
- Delivery task tracking
- Email verification endpoints
- Notification APIs
- Review and rating APIs
- Admin management APIs
- Order cancellation
- Order history endpoints

---

## Postman Collection

A Postman collection with all endpoints will be provided separately for easy API testing.

---

## WebSocket APIs (Future)

### Real-Time Order Tracking
**Endpoint**: `ws://localhost:8080/ws/orders/{orderId}`  
**Description**: Subscribe to real-time order status updates

**Message Format**:
```json
{
  "type": "STATUS_UPDATE",
  "orderId": 1,
  "newStatus": "PICKED_UP",
  "timestamp": "2026-03-28T10:15:00"
}
```

### Real-Time Rider Location
**Endpoint**: `ws://localhost:8080/ws/riders/{riderId}/location`  
**Description**: Subscribe to rider location updates

---

## API Versioning Strategy

- Current version: v1
- Version in URL path: `/api/v1/...`
- Breaking changes will increment version: `/api/v2/...`
- Old versions supported for 6 months after new version release

---

## Security Headers

All API responses include security headers:
```
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains
```

---

## CORS Configuration

Allowed origins (configurable):
- `http://localhost:3000` (development)
- `https://kaldar.com` (production)

Allowed methods: GET, POST, PUT, PATCH, DELETE  
Allowed headers: Authorization, Content-Type

---

## Next Steps

1. Implement missing endpoints marked as "To Be Implemented"
2. Add OpenAPI/Swagger documentation
3. Create Postman collection
4. Add API integration tests
5. Implement rate limiting
6. Add API monitoring and logging

---

**Last Updated**: March 27, 2026
