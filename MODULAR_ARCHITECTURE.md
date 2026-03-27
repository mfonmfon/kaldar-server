# Kaldar - Modular Monolith Architecture

## Overview
This application follows a **Modular Monolith** architectural pattern, organizing code into cohesive, loosely-coupled modules with clear boundaries and responsibilities.

## Module Structure

```
com.kaldar.kaldar/
├── shared/                          # Shared Kernel (cross-module utilities)
│   ├── domain/
│   │   ├── constants/               # OrderStatus, Role, ClothType, StatusResponse
│   │   ├── events/                  # Domain events for inter-module communication
│   │   ├── exceptions/              # Common exceptions
│   │   ├── model/                   # UserEntity, Admins
│   │   ├── repository/              # UserEntityRepository
│   │   └── valueobjects/            # Location, Money
│   ├── infrastructure/
│   │   ├── auth/                    # Authentication & authorization
│   │   │   ├── api/                 # AuthenticationController, VerificationTokenController
│   │   │   ├── domain/              # VerificationToken, PasswordResetToken
│   │   │   ├── dto/                 # Auth DTOs
│   │   │   └── service/             # JwtService, AuthenticationService, PasswordResetService
│   │   ├── config/                  # Application configuration
│   │   ├── email/                   # Email service
│   │   ├── security/                # JWT filter, SecurityConfiguration
│   │   └── utility/                 # OtpGenerator, Mapper
│   └── api/
│       ├── handler/                 # GlobalApplicationExceptionHandler
│       ├── response/                # ApiResponse, ApiErrorResponse
│       └── ApiResponseWrapper       # Generic response wrapper
│
├── customer/                        # Customer Module
│   ├── api/                         # CustomerController
│   ├── application/
│   │   ├── dto/                     # Customer DTOs
│   │   └── service/                 # CustomerService, DefaultCustomerService
│   └── domain/
│       ├── model/                   # CustomerEntity
│       └── repository/              # CustomerEntityRepository
│
├── drycleaner/                      # Dry Cleaner Module
│   ├── api/                         # DryCleanerController
│   ├── application/
│   │   ├── dto/                     # DryClean DTOs
│   │   └── service/                 # DryCleanerService, DefaultDryCleanerService
│   └── domain/
│       ├── model/                   # DryCleanerEntity, ServiceOffering
│       └── repository/              # DryCleanerEntityRepository, ServiceOfferingRepository
│
├── discovery/                       # Discovery/Matching Module
│   ├── api/                         # DiscoveryController
│   ├── application/
│   │   ├── dto/                     # Discovery DTOs
│   │   └── service/                 # DryCleanerQueryService, DefaultDryCleanerQueryService
│   └── domain/
│       └── service/                 # Matching algorithms (to be implemented)
│
├── order/                           # Order Management Module
│   ├── api/                         # OrderController
│   ├── application/
│   │   ├── dto/                     # Order DTOs
│   │   └── service/                 # OrderService, DefaultOrderService
│   └── domain/
│       ├── model/                   # OrderEntity, OrderServiceItem
│       └── repository/              # OrderEntityRepository, OrderServiceItemRepository
│
└── dispatch/                        # Dispatch/Logistics Module
    ├── api/                         # (To be implemented)
    ├── application/
    │   ├── dto/                     # (To be implemented)
    │   └── service/                 # (To be implemented)
    └── domain/
        ├── model/                   # DispatchRider
        └── repository/              # (To be implemented)
```

## Module Descriptions

### 1. Shared Module
**Purpose**: Contains cross-cutting concerns and utilities shared across all modules.

**Key Components**:
- Domain constants (OrderStatus, Role, ClothType)
- Common exceptions
- Value objects (Location, Money)
- Authentication & authorization infrastructure
- Email service
- Security configuration
- Global exception handler

**Dependencies**: None (foundation module)

---

### 2. Customer Module
**Purpose**: Manages customer lifecycle and profile.

**Responsibilities**:
- Customer registration
- Profile management
- Password management
- Customer authentication

**Key Entities**: CustomerEntity

**Dependencies**: Shared module

---

### 3. Dry Cleaner Module
**Purpose**: Manages dry cleaner businesses and their service offerings.

**Responsibilities**:
- Dry cleaner registration
- Business profile management
- Service catalog management (pricing, services)
- Order acceptance/rejection

**Key Entities**: DryCleanerEntity, ServiceOffering

**Dependencies**: Shared module

---

### 4. Discovery Module
**Purpose**: Matches customers with nearby dry cleaners based on location and services.

**Responsibilities**:
- Location-based search
- Price calculation and quotes
- Service availability matching
- Ranking dry cleaners by distance/price

**Key Features**:
- Geospatial queries (using Location value object)
- Price aggregation
- Availability filtering

**Dependencies**: Shared, DryClean modules

---

### 5. Order Module
**Purpose**: Manages the complete order lifecycle.

**Responsibilities**:
- Order creation
- Order status transitions (state machine)
- Order item management
- Order history and tracking

**Key Entities**: OrderEntity, OrderServiceItem

**State Machine**: PENDING_ACCEPTANCE → ACCEPTED → SCHEDULED → PICKED_UP → CLEANING → READY_FOR_DELIVERY → OUT_FOR_DELIVERY → DELIVERED → COMPLETED

**Dependencies**: Shared, Customer, DryClean modules

---

### 6. Dispatch Module
**Purpose**: Manages logistics and rider operations.

**Responsibilities** (To be implemented):
- Rider registration and management
- Pickup task assignment
- Delivery task assignment
- Delivery tracking

**Key Entities**: DispatchRider, DeliveryTask (to be created)

**Dependencies**: Shared, Order modules

---

## Module Communication Patterns

### 1. Direct Dependencies
Modules can directly call services from other modules when needed:
```java
// Order module calling Customer repository
@Autowired
private CustomerEntityRepository customerRepository;
```

### 2. Domain Events (Recommended for decoupling)
Use Spring's ApplicationEventPublisher for async communication:
```java
// Publishing event
applicationEventPublisher.publishEvent(new OrderAcceptedEvent(orderId));

// Listening to event
@EventListener
public void handleOrderAccepted(OrderAcceptedEvent event) {
    // Assign rider
}
```

### 3. REST APIs
External clients communicate via REST endpoints in each module's `api` package.

---

## Design Principles

### 1. High Cohesion
Each module contains related functionality. All customer-related code is in the customer module.

### 2. Loose Coupling
Modules depend on interfaces, not implementations. Communication via events when possible.

### 3. Clear Boundaries
Each module has its own:
- Domain models
- Repositories
- Services
- DTOs
- Controllers

### 4. Shared Kernel
Common utilities in the shared module prevent duplication.

### 5. Dependency Direction
```
Customer ──┐
           ├──> Shared
DryClean ──┤
           │
Discovery ─┴──> DryClean
           │
Order ─────┴──> Customer, DryClean
           │
Dispatch ──┴──> Order
```

---

## Benefits of This Architecture

1. **Maintainability**: Easy to locate and modify code
2. **Scalability**: Modules can be extracted into microservices later
3. **Team Collaboration**: Teams can work on different modules independently
4. **Testability**: Modules can be tested in isolation
5. **Clear Ownership**: Each module has a clear purpose
6. **Flexibility**: Easy to add new modules without affecting existing ones

---

## Next Steps

1. ✅ Restructure existing code into modules
2. ⏳ Implement discovery/matching logic with geospatial queries
3. ⏳ Implement dispatch module with rider assignment
4. ⏳ Add domain events for inter-module communication
5. ⏳ Implement notification module
6. ⏳ Add integration tests for each module

---

## Migration Notes

All existing code has been reorganized into the modular structure:
- Controllers moved to `{module}/api/`
- Services moved to `{module}/application/service/`
- Entities moved to `{module}/domain/model/`
- Repositories moved to `{module}/domain/repository/`
- DTOs moved to `{module}/application/dto/`
- Shared utilities moved to `shared/`

Import statements will need to be updated to reflect the new package structure.
