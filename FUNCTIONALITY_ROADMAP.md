# Kaldar Functionality Roadmap

## Complete Feature List for Dry Cleaning Marketplace

### ✅ IMPLEMENTED (Current State)

#### Authentication & Authorization
- ✅ Customer registration with email verification (OTP)
- ✅ Dry cleaner registration with email verification (OTP)
- ✅ JWT-based authentication
- ✅ Password reset flow
- ✅ OTP resend functionality
- ✅ Role-based access control (CUSTOMER, DRY_CLEANER, RIDER, ADMIN)

#### Customer Management
- ✅ Customer profile management (update name, phone, address)
- ✅ Change password
- ✅ View profile

#### Dry Cleaner Management
- ✅ Dry cleaner business profile management
- ✅ Service offerings catalog (add/update services with pricing)
- ✅ Accept/reject orders
- ✅ Business location (latitude/longitude)

#### Order Management
- ✅ Create order with items
- ✅ Order status state machine (13 states)
- ✅ Order item management
- ✅ Price calculation and snapshot
- ✅ Update order status

#### Discovery
- ✅ Basic dry cleaner query
- ✅ Pagination support

---

### ⏳ TO BE IMPLEMENTED

#### 1. DISCOVERY MODULE - Core Feature (HIGH PRIORITY)

**1.1 Geospatial Search**
```java
// Find dry cleaners within radius
Page<DryCleanerMatch> findNearbyDryCleaners(
    Double customerLatitude,
    Double customerLongitude,
    Double radiusKm,
    List<ClothType> clothTypes,
    Pageable pageable
);
```

**Features**:
- Distance calculation using Haversine formula (already in Location value object)
- Filter by radius (e.g., 5km, 10km, 20km)
- Filter by service availability (cloth types)
- Filter by working hours
- Filter by active status

**1.2 Price Quote Calculation**
```java
// Calculate total price for each dry cleaner
DryCleanerQuote calculateQuote(
    Long dryCleanerId,
    List<OrderItemRequest> items
);
```

**Features**:
- Aggregate prices for all selected items
- Show per-item breakdown
- Include express service pricing (if available)
- Show estimated turnaround time

**1.3 Ranking Algorithm**
```java
// Rank dry cleaners by multiple factors
List<DryCleanerMatch> rankDryCleaners(
    List<DryCleanerEntity> drycleaners,
    RankingCriteria criteria
);
```

**Ranking Factors**:
- Distance (closer is better)
- Price (lower is better)
- Rating (higher is better) - future
- Availability (working hours match)
- Estimated turnaround time

**1.4 Response Structure**
```java
public class DryCleanerMatch {
    private Long dryCleanerId;
    private String businessName;
    private String businessAddress;
    private Double distanceKm;
    private Double totalPrice;
    private List<ServiceQuote> serviceBreakdown;
    private Integer estimatedTurnaroundHours;
    private String workingHours;
    private Double rating; // future
}
```

---

#### 2. DISPATCH MODULE - Logistics (HIGH PRIORITY)

**2.1 Rider Management**
```java
public interface RiderService {
    void registerRider(RiderRegistrationRequest request);
    void updateAvailability(Long riderId, boolean available);
    void updateLocation(Long riderId, Double lat, Double lon);
}
```

**2.2 Automatic Rider Assignment**
```java
public interface DispatchService {
    // Assign rider when order is accepted
    void assignRiderForPickup(Long orderId);
    
    // Assign rider when order is ready
    void assignRiderForDelivery(Long orderId);
    
    // Find nearest available rider
    DispatchRider findNearestAvailableRider(Location location);
}
```

**2.3 Delivery Task Management**
```java
@Entity
public class DeliveryTask {
    private Long id;
    private OrderEntity order;
    private DispatchRider rider;
    private DeliveryType type; // PICKUP or DELIVERY
    private DeliveryStatus status; // ASSIGNED, IN_TRANSIT, COMPLETED
    private Location pickupLocation;
    private Location dropoffLocation;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
}
```

**2.4 Event Listeners**
```java
@EventListener
public void handleOrderAccepted(OrderAcceptedEvent event) {
    // Auto-assign rider for pickup
    dispatchService.assignRiderForPickup(event.getOrderId());
}

@EventListener
public void handleOrderReady(OrderReadyEvent event) {
    // Auto-assign rider for delivery
    dispatchService.assignRiderForDelivery(event.getOrderId());
}
```

---

#### 3. NOTIFICATION MODULE (MEDIUM PRIORITY)

**3.1 Notification Service**
```java
public interface NotificationService {
    void sendOrderConfirmation(Long orderId);
    void sendOrderStatusUpdate(Long orderId, OrderStatus newStatus);
    void sendRiderAssignment(Long orderId, Long riderId);
    void sendPickupReminder(Long orderId);
    void sendDeliveryReminder(Long orderId);
}
```

**3.2 Notification Channels**
- Email (already implemented)
- SMS (to implement)
- Push notifications (future)
- In-app notifications (future)

**3.3 Event-Driven Notifications**
```java
@EventListener
public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
    notificationService.sendOrderStatusUpdate(
        event.getOrderId(), 
        event.getNewStatus()
    );
}
```

---

#### 4. ENHANCED ORDER FLOW (MEDIUM PRIORITY)

**4.1 Order Workflow Improvements**
- Add order cancellation with rules (can only cancel before PICKED_UP)
- Add order modification (before acceptance)
- Add special instructions field
- Add photo upload for clothes (future)

**4.2 Pricing Enhancements**
- Dynamic pricing based on demand
- Surge pricing during peak hours
- Discount codes/coupons
- Loyalty points

**4.3 Scheduling**
- Validate pickup/delivery time slots
- Check dry cleaner availability
- Prevent overbooking
- Time slot suggestions

---

#### 5. RATING & REVIEW SYSTEM (LOW PRIORITY)

**5.1 Entities**
```java
@Entity
public class Review {
    private Long id;
    private OrderEntity order;
    private CustomerEntity customer;
    private DryCleanerEntity dryCleaner;
    private Integer rating; // 1-5 stars
    private String comment;
    private LocalDateTime createdAt;
}
```

**5.2 Features**
- Customer rates dry cleaner after delivery
- Customer rates rider
- Average rating calculation
- Review moderation

---

#### 6. PAYMENT INTEGRATION (LOW PRIORITY)

**6.1 Payment Module**
```java
public interface PaymentService {
    PaymentIntent createPaymentIntent(Long orderId);
    void processPayment(Long orderId, String paymentMethod);
    void refundPayment(Long orderId);
}
```

**6.2 Payment Providers**
- Paystack (Nigeria)
- Flutterwave
- Stripe (international)

**6.3 Payment Flow**
- Payment on order creation (prepaid)
- Payment on delivery (cash/card)
- Wallet system
- Payment history

---

#### 7. ADMIN MODULE (LOW PRIORITY)

**7.1 Admin Dashboard**
- View all orders
- View all customers
- View all dry cleaners
- View all riders
- System analytics

**7.2 Admin Actions**
- Approve/reject dry cleaner registrations
- Suspend users
- Resolve disputes
- Manual rider assignment

---

#### 8. REAL-TIME TRACKING (FUTURE)

**8.1 Live Location Tracking**
- Rider real-time location
- ETA calculation
- Customer tracking interface

**8.2 WebSocket Integration**
- Real-time order status updates
- Live rider location
- Chat between customer and rider

---

#### 9. ANALYTICS & REPORTING (FUTURE)

**9.1 Business Analytics**
- Order volume trends
- Revenue analytics
- Popular services
- Peak hours analysis
- Customer retention metrics

**9.2 Dry Cleaner Analytics**
- Performance metrics
- Order completion rate
- Average turnaround time
- Revenue per dry cleaner

---

## Implementation Priority

### Phase 1: MVP (Weeks 1-2)
1. ✅ Module restructuring (DONE)
2. ⏳ Discovery module - geospatial search
3. ⏳ Discovery module - price quote calculation
4. ⏳ Dispatch module - basic rider assignment
5. ⏳ Domain events setup

### Phase 2: Core Features (Weeks 3-4)
1. ⏳ Automatic rider assignment on order acceptance
2. ⏳ Automatic rider assignment on order ready
3. ⏳ Notification module - email notifications
4. ⏳ Enhanced order flow (cancellation, modification)
5. ⏳ Delivery task tracking

### Phase 3: Enhancements (Weeks 5-6)
1. ⏳ Rating & review system
2. ⏳ SMS notifications
3. ⏳ Admin dashboard
4. ⏳ Payment integration
5. ⏳ Advanced scheduling

### Phase 4: Advanced Features (Weeks 7-8)
1. ⏳ Real-time tracking
2. ⏳ WebSocket integration
3. ⏳ Analytics dashboard
4. ⏳ Push notifications
5. ⏳ Chat system

---

## Technical Implementation Notes

### Geospatial Queries

**Option 1: PostGIS (Recommended)**
```sql
-- Find dry cleaners within 10km
SELECT * FROM dry_cleaner_entity
WHERE ST_DWithin(
    ST_MakePoint(longitude, latitude)::geography,
    ST_MakePoint(?, ?)::geography,
    10000  -- meters
)
AND is_active = true
AND verified_user = true;
```

**Option 2: Haversine in Java (Current)**
```java
// Already implemented in Location value object
double distance = customerLocation.distanceTo(dryCleanerLocation);
if (distance <= radiusKm) {
    // Include in results
}
```

### Event-Driven Architecture

```java
// 1. Define events in shared/domain/events/
public class OrderAcceptedEvent extends DomainEvent {
    private final Long orderId;
    private final Long dryCleanerId;
    // ...
}

// 2. Publish events
applicationEventPublisher.publishEvent(
    new OrderAcceptedEvent(orderId, dryCleanerId)
);

// 3. Listen to events in other modules
@Component
public class DispatchEventListener {
    @EventListener
    @Async
    public void handleOrderAccepted(OrderAcceptedEvent event) {
        dispatchService.assignRiderForPickup(event.getOrderId());
    }
}
```

### State Machine Validation

```java
// Already implemented in DefaultOrderService
private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS;

// Validates transitions
if (!isTransitionAllowed(currentStatus, newStatus)) {
    throw new InvalidOrderAssignmentException(
        "Transition not allowed: " + currentStatus + " -> " + newStatus
    );
}
```

---

## API Endpoints to Implement

### Discovery API
```
POST   /api/discovery/search
       Request: { latitude, longitude, clothTypes[], quantities[], pickupTime, deliveryTime }
       Response: [ { dryCleanerId, businessName, distance, totalPrice, breakdown[] } ]

GET    /api/discovery/drycleaners/{id}/quote
       Query: clothTypes, quantities
       Response: { totalPrice, breakdown[], estimatedTime }
```

### Dispatch API
```
POST   /api/dispatch/riders/register
GET    /api/dispatch/riders/{id}
PATCH  /api/dispatch/riders/{id}/availability
PATCH  /api/dispatch/riders/{id}/location
GET    /api/dispatch/tasks
GET    /api/dispatch/tasks/{id}
PATCH  /api/dispatch/tasks/{id}/complete
```

### Notification API
```
POST   /api/notifications/send
GET    /api/notifications/history
```

---

## Database Schema Additions Needed

### Delivery Tasks Table
```sql
CREATE TABLE delivery_task (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES order_entity(id),
    rider_id BIGINT REFERENCES dispatch_rider(id),
    task_type VARCHAR(20), -- PICKUP or DELIVERY
    status VARCHAR(20),
    pickup_latitude DOUBLE PRECISION,
    pickup_longitude DOUBLE PRECISION,
    dropoff_latitude DOUBLE PRECISION,
    dropoff_longitude DOUBLE PRECISION,
    assigned_at TIMESTAMP,
    completed_at TIMESTAMP
);
```

### Reviews Table
```sql
CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES order_entity(id),
    customer_id BIGINT REFERENCES customer_entity(id),
    dry_cleaner_id BIGINT REFERENCES dry_cleaner_entity(id),
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP
);
```

### Notifications Table
```sql
CREATE TABLE notification_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    type VARCHAR(50),
    channel VARCHAR(20), -- EMAIL, SMS, PUSH
    content TEXT,
    sent_at TIMESTAMP,
    status VARCHAR(20)
);
```

---

## Configuration Additions Needed

### application.properties
```properties
# Geospatial
kaldar.discovery.default-radius-km=10
kaldar.discovery.max-radius-km=50

# Dispatch
kaldar.dispatch.auto-assign-enabled=true
kaldar.dispatch.rider-search-radius-km=5

# Notifications
kaldar.notification.email.enabled=true
kaldar.notification.sms.enabled=false
kaldar.notification.sms.provider=twilio
kaldar.notification.sms.api-key=${SMS_API_KEY}

# Payment
kaldar.payment.provider=paystack
kaldar.payment.api-key=${PAYMENT_API_KEY}
kaldar.payment.webhook-secret=${PAYMENT_WEBHOOK_SECRET}
```

---

## Testing Strategy

### Unit Tests
- Test each service in isolation
- Mock dependencies
- Test business logic

### Integration Tests
- Test module interactions
- Test database operations
- Test event publishing/listening

### End-to-End Tests
- Test complete user flows
- Test API endpoints
- Test authentication

---

## Performance Considerations

### Database Optimization
- Add indexes on frequently queried fields:
  - `dry_cleaner_entity(latitude, longitude)` - Spatial index
  - `order_entity(customer_id, created_at)` - Order history
  - `order_entity(dry_cleaner_id, order_status)` - Dry cleaner orders
  - `delivery_task(rider_id, status)` - Rider tasks

### Caching Strategy
- Cache dry cleaner service offerings (rarely change)
- Cache user profiles (update on change)
- Cache geospatial queries (short TTL)

### Async Processing
- Send notifications asynchronously
- Process rider assignments asynchronously
- Generate analytics reports asynchronously

---

## Security Considerations

### API Security
- Rate limiting on public endpoints
- Input validation on all requests
- SQL injection prevention (use JPA)
- XSS prevention

### Data Privacy
- Encrypt sensitive data (passwords already hashed)
- GDPR compliance (data export, deletion)
- PII protection

### Authorization
- Customers can only view their own orders
- Dry cleaners can only view assigned orders
- Riders can only view assigned tasks
- Admins have full access

---

## Monitoring & Observability

### Logging
- Structured logging (JSON format)
- Log levels: ERROR, WARN, INFO, DEBUG
- Log aggregation (ELK stack)

### Metrics
- Order creation rate
- Order completion rate
- Average delivery time
- Rider utilization
- System health

### Alerts
- Failed order creations
- Unassigned orders
- Rider unavailability
- Payment failures

---

## Deployment Strategy

### Development
```bash
./mvnw.cmd spring-boot:run
```

### Staging
```bash
./mvnw.cmd clean package
java -jar target/kaldar-0.0.1-SNAPSHOT.jar --spring.profiles.active=staging
```

### Production
```bash
docker-compose up -d
```

---

## Next Immediate Steps

1. **Implement Discovery Geospatial Search** (1-2 days)
   - Add distance calculation to query
   - Filter by radius
   - Return sorted by distance

2. **Implement Price Quote Calculation** (1 day)
   - Aggregate service prices
   - Calculate total for each dry cleaner
   - Return with breakdown

3. **Implement Rider Assignment** (2-3 days)
   - Create DeliveryTask entity
   - Implement assignment algorithm
   - Add event listeners

4. **Add Domain Events** (1 day)
   - OrderAcceptedEvent
   - OrderReadyEvent
   - RiderAssignedEvent
   - OrderCompletedEvent

5. **Implement Notifications** (2 days)
   - Email notifications for status changes
   - SMS integration (optional)
   - Event-driven notification triggers

---

## Success Criteria

### MVP Launch Checklist
- ✅ Module restructuring complete
- ⏳ Customer can search nearby dry cleaners
- ⏳ Customer can see price quotes
- ⏳ Customer can place order
- ⏳ Dry cleaner can accept order
- ⏳ Rider automatically assigned for pickup
- ⏳ Rider automatically assigned for delivery
- ⏳ Notifications sent at each status change
- ⏳ Order can be tracked end-to-end

### Performance Targets
- API response time < 200ms (95th percentile)
- Geospatial search < 100ms
- Order creation < 500ms
- Rider assignment < 1s

### Quality Targets
- Code coverage > 80%
- Zero critical security vulnerabilities
- All endpoints documented (OpenAPI/Swagger)
- All modules have integration tests

---

Your application is now ready for feature development with a solid modular foundation! 🚀
