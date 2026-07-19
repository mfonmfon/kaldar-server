package com.kaldar.kaldar.order.service;

import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.customer.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import com.kaldar.kaldar.drycleaner.domain.model.ServiceOffering;
import com.kaldar.kaldar.drycleaner.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.drycleaner.domain.repository.ServiceOfferingRepository;
import com.kaldar.kaldar.order.application.dto.request.*;
import com.kaldar.kaldar.order.application.dto.response.*;
import com.kaldar.kaldar.order.application.service.impl.DefaultOrderService;
import com.kaldar.kaldar.order.domain.model.OrderEntity;
import com.kaldar.kaldar.order.domain.model.OrderServiceItem;
import com.kaldar.kaldar.order.domain.model.ReviewEntity;
import com.kaldar.kaldar.order.domain.repository.OrderEntityRepository;
import com.kaldar.kaldar.order.domain.repository.OrderServiceItemRepository;
import com.kaldar.kaldar.order.domain.repository.ReviewRepository;
import com.kaldar.kaldar.shared.domain.constants.OrderStatus;
import com.kaldar.kaldar.shared.domain.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultOrderService Unit Tests")
class DefaultOrderServiceTest {

    @Mock private CustomerEntityRepository customerEntityRepository;
    @Mock private DryCleanerEntityRepository dryCleanerEntityRepository;
    @Mock private ServiceOfferingRepository serviceOfferingRepository;
    @Mock private OrderEntityRepository orderEntityRepository;
    @Mock private OrderServiceItemRepository orderServiceItemRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private ReviewRepository reviewRepository;

    private DefaultOrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new DefaultOrderService(
                customerEntityRepository, dryCleanerEntityRepository, serviceOfferingRepository,
                orderEntityRepository, orderServiceItemRepository, eventPublisher, reviewRepository
        );
    }

    // =========================================================================
    // Helper Factories
    // =========================================================================

    private CustomerEntity buildCustomer() {
        CustomerEntity c = new CustomerEntity();
        c.setId(1L);
        c.setFirstName("John");
        c.setLastName("Doe");
        return c;
    }

    private DryCleanerEntity buildDryCleaner() {
        DryCleanerEntity dc = new DryCleanerEntity();
        dc.setId(10L);
        dc.setBusinessName("Sparkle Cleaners");
        dc.setActive(true);
        ServiceOffering offering = new ServiceOffering();
        offering.setId(100L);
        offering.setServiceName("Shirt Cleaning");
        offering.setUnitPrice(1500.0);
        List<ServiceOffering> offerings = new ArrayList<>();
        offerings.add(offering);
        dc.setServiceOfferings(offerings);
        return dc;
    }

    private ServiceOffering buildServiceOffering(DryCleanerEntity dryCleaner) {
        ServiceOffering offering = new ServiceOffering();
        offering.setId(100L);
        offering.setServiceName("Shirt Cleaning");
        offering.setUnitPrice(1500.0);
        offering.setDryCleaner(dryCleaner);
        return offering;
    }

    private OrderEntity buildPendingOrder(CustomerEntity customer, DryCleanerEntity dryCleaner) {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setCustomer(customer);
        order.setDryCleaner(dryCleaner);
        order.setOrderStatus(OrderStatus.PENDING_ACCEPTANCE);
        order.setPickupAddress("123 Main St");
        order.setDeliveryAddress("123 Main St");
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderServiceItems(new ArrayList<>());
        return order;
    }

    
    // placeOrder
   
    @Nested
    @DisplayName("placeOrder()")
    class PlaceOrder {

        @Test
        @DisplayName("should create order successfully with valid service items")
        void shouldCreateOrderSuccessfully() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            ServiceOffering offering = buildServiceOffering(dryCleaner);

            when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(dryCleanerEntityRepository.findById(10L)).thenReturn(Optional.of(dryCleaner));
            when(serviceOfferingRepository.findById(100L)).thenReturn(Optional.of(offering));
            when(orderEntityRepository.save(any(OrderEntity.class))).thenAnswer(inv -> {
                OrderEntity o = inv.getArgument(0);
                o.setId(50L);
                return o;
            });

            OrderItemRequest item = new OrderItemRequest();
            item.setServiceOfferingId(100L);
            item.setQuantity(3);

            CreateOrderRequest request = new CreateOrderRequest();
            request.setCustomerId(1L);
            request.setDryCleanerId(10L);
            request.setPickupAddress("123 Main St");
            request.setDeliveryAddress("123 Main St");
            request.setServiceItems(List.of(item));

            CreateOrderResponse response = orderService.placeOrder(request);

            assertThat(response).isNotNull();
            assertThat(response.getOrderId()).isEqualTo(50L);
            assertThat(response.getTotalPrice()).isEqualTo(4500.0); // 1500 * 3
            assertThat(response.getCustomerId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when customer does not exist")
        void shouldThrowWhenCustomerNotFound() {
            when(customerEntityRepository.findById(99L)).thenReturn(Optional.empty());

            CreateOrderRequest request = new CreateOrderRequest();
            request.setCustomerId(99L);

            assertThatThrownBy(() -> orderService.placeOrder(request))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when dry cleaner does not exist")
        void shouldThrowWhenDryCleanerNotFound() {
            when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(buildCustomer()));
            when(dryCleanerEntityRepository.findById(999L)).thenReturn(Optional.empty());

            CreateOrderRequest request = new CreateOrderRequest();
            request.setCustomerId(1L);
            request.setDryCleanerId(999L);
            request.setServiceItems(new ArrayList<>());

            assertThatThrownBy(() -> orderService.placeOrder(request))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("should throw EmptyRequiredFieldException when customerId is null")
        void shouldThrowWhenCustomerIdIsNull() {
            CreateOrderRequest request = new CreateOrderRequest();
            request.setCustomerId(null);

            assertThatThrownBy(() -> orderService.placeOrder(request))
                    .isInstanceOf(EmptyRequiredFieldException.class)
                    .hasMessageContaining("Customer id is required");
        }

        @Test
        @DisplayName("should throw EmptyRequiredFieldException when item quantity is zero")
        void shouldThrowWhenItemQuantityIsZero() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();

            when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(dryCleanerEntityRepository.findById(10L)).thenReturn(Optional.of(dryCleaner));
            // NOTE: serviceOfferingRepository.findById is NOT stubbed here because the quantity=0
            // check throws EmptyRequiredFieldException BEFORE the repository is ever called.

            OrderItemRequest item = new OrderItemRequest();
            item.setServiceOfferingId(100L);
            item.setQuantity(0); // invalid — triggers check before repo lookup

            CreateOrderRequest request = new CreateOrderRequest();
            request.setCustomerId(1L);
            request.setDryCleanerId(10L);
            request.setServiceItems(List.of(item));

            assertThatThrownBy(() -> orderService.placeOrder(request))
                    .isInstanceOf(EmptyRequiredFieldException.class)
                    .hasMessageContaining("quantity must be greater than zero");
        }

        @Test
        @DisplayName("should throw InvalidOrderAssignmentException when service belongs to different dry cleaner")
        void shouldThrowWhenServiceBelongsToDifferentDryCleaner() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();

            DryCleanerEntity otherDc = new DryCleanerEntity();
            otherDc.setId(999L);
            ServiceOffering offeringFromOtherDc = new ServiceOffering();
            offeringFromOtherDc.setId(200L);
            offeringFromOtherDc.setDryCleaner(otherDc);
            offeringFromOtherDc.setUnitPrice(1000.0);

            when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(dryCleanerEntityRepository.findById(10L)).thenReturn(Optional.of(dryCleaner));
            when(serviceOfferingRepository.findById(200L)).thenReturn(Optional.of(offeringFromOtherDc));

            OrderItemRequest item = new OrderItemRequest();
            item.setServiceOfferingId(200L);
            item.setQuantity(2);

            CreateOrderRequest request = new CreateOrderRequest();
            request.setCustomerId(1L);
            request.setDryCleanerId(10L);
            request.setServiceItems(List.of(item));

            assertThatThrownBy(() -> orderService.placeOrder(request))
                    .isInstanceOf(InvalidOrderAssignmentException.class)
                    .hasMessageContaining("does not belong to selected dry cleaner");
        }

        @Test
        @DisplayName("should set order status to PENDING_ACCEPTANCE on creation")
        void shouldSetPendingAcceptanceStatus() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(dryCleanerEntityRepository.findById(10L)).thenReturn(Optional.of(dryCleaner));
            when(orderEntityRepository.save(any())).thenAnswer(inv -> {
                OrderEntity o = inv.getArgument(0);
                o.setId(1L);
                return o;
            });

            CreateOrderRequest request = new CreateOrderRequest();
            request.setCustomerId(1L);
            request.setDryCleanerId(10L);
            request.setServiceItems(new ArrayList<>());

            orderService.placeOrder(request);

            ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
            verify(orderEntityRepository).save(captor.capture());
            assertThat(captor.getValue().getOrderStatus()).isEqualTo(OrderStatus.PENDING_ACCEPTANCE);
        }
    }

    
    // acceptOrder

    @Nested
    @DisplayName("acceptOrder()")
    class AcceptOrder {

        @Test
        @DisplayName("should accept an order that is PENDING_ACCEPTANCE")
        void shouldAcceptOrderSuccessfully() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            OrderEntity order = buildPendingOrder(customer, dryCleaner);

            when(dryCleanerEntityRepository.findById(10L)).thenReturn(Optional.of(dryCleaner));
            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            AcceptOrderRequest request = new AcceptOrderRequest();
            request.setOrderId(1L);
            request.setDryCleanerId(10L);

            AcceptOrderResponse response = orderService.acceptOrder(request);

            assertThat(response.getOrderId()).isEqualTo(1L);
            assertThat(response.getStatus()).isEqualTo("ACCEPTED");
            assertThat(response.getTimestamp()).isNotNull();

            ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
            verify(orderEntityRepository).save(captor.capture());
            assertThat(captor.getValue().getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        @DisplayName("should throw when order is not assigned to given dry cleaner")
        void shouldThrowWhenOrderNotAssignedToDryCleaner() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            DryCleanerEntity wrongDc = new DryCleanerEntity();
            wrongDc.setId(999L);
            wrongDc.setActive(true);

            OrderEntity order = buildPendingOrder(customer, dryCleaner); // assigned to dc id=10

            when(dryCleanerEntityRepository.findById(999L)).thenReturn(Optional.of(wrongDc));
            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));

            AcceptOrderRequest request = new AcceptOrderRequest();
            request.setOrderId(1L);
            request.setDryCleanerId(999L);

            assertThatThrownBy(() -> orderService.acceptOrder(request))
                    .isInstanceOf(InvalidOrderAssignmentException.class)
                    .hasMessageContaining("Order not assigned to this drycleaner");
        }

        @Test
        @DisplayName("should throw when order is not in PENDING_ACCEPTANCE status")
        void shouldThrowWhenOrderIsAlreadyAccepted() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            OrderEntity order = buildPendingOrder(customer, dryCleaner);
            order.setOrderStatus(OrderStatus.ACCEPTED); // already accepted

            when(dryCleanerEntityRepository.findById(10L)).thenReturn(Optional.of(dryCleaner));
            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));

            AcceptOrderRequest request = new AcceptOrderRequest();
            request.setOrderId(1L);
            request.setDryCleanerId(10L);

            assertThatThrownBy(() -> orderService.acceptOrder(request))
                    .isInstanceOf(InvalidOrderAssignmentException.class)
                    .hasMessageContaining("Order cannot be accepted");
        }

        @Test
        @DisplayName("should throw NoActiveDryCleanerException when dry cleaner is inactive")
        void shouldThrowWhenDryCleanerIsInactive() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            dryCleaner.setActive(false); // inactive
            OrderEntity order = buildPendingOrder(customer, dryCleaner);

            when(dryCleanerEntityRepository.findById(10L)).thenReturn(Optional.of(dryCleaner));
            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));

            AcceptOrderRequest request = new AcceptOrderRequest();
            request.setOrderId(1L);
            request.setDryCleanerId(10L);

            assertThatThrownBy(() -> orderService.acceptOrder(request))
                    .isInstanceOf(NoActiveDryCleanerException.class);
        }

        @Test
        @DisplayName("should use provided pickupAt time when supplied")
        void shouldUseProvidedPickupAt() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            OrderEntity order = buildPendingOrder(customer, dryCleaner);

            LocalDateTime pickupTime = LocalDateTime.now().plusHours(2);
            when(dryCleanerEntityRepository.findById(10L)).thenReturn(Optional.of(dryCleaner));
            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            AcceptOrderRequest request = new AcceptOrderRequest();
            request.setOrderId(1L);
            request.setDryCleanerId(10L);
            request.setPickupAt(pickupTime);

            orderService.acceptOrder(request);

            ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
            verify(orderEntityRepository).save(captor.capture());
            assertThat(captor.getValue().getPickupAt()).isEqualTo(pickupTime);
        }
    }

    //reject order

    @Nested
    @DisplayName("rejectOrder()")
    class RejectOrder {

        @Test
        @DisplayName("should reject an order successfully")
        void shouldRejectOrderSuccessfully() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            OrderEntity order = buildPendingOrder(customer, dryCleaner);

            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));
            when(dryCleanerEntityRepository.findById(10L)).thenReturn(Optional.of(dryCleaner));
            when(orderEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            RejectOrderRequest request = new RejectOrderRequest();
            request.setOrderId(1L);
            request.setDryCleanerId(10L);
            request.setReason("At full capacity");

            RejectOrderResponse response = orderService.rejectOrder(request);

            assertThat(response.getStatus()).isEqualTo("REJECTED");
            assertThat(response.getOrderId()).isEqualTo(1L);

            ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
            verify(orderEntityRepository).save(captor.capture());
            assertThat(captor.getValue().getOrderStatus()).isEqualTo(OrderStatus.REJECTED);
            assertThat(captor.getValue().getRejectionReason()).isEqualTo("At full capacity");
        }

        @Test
        @DisplayName("should throw when order is not in PENDING_ACCEPTANCE state")
        void shouldThrowWhenOrderCannotBeRejected() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            OrderEntity order = buildPendingOrder(customer, dryCleaner);
            order.setOrderStatus(OrderStatus.CLEANING); // not rejectable

            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));
            when(dryCleanerEntityRepository.findById(10L)).thenReturn(Optional.of(dryCleaner));

            RejectOrderRequest request = new RejectOrderRequest();
            request.setOrderId(1L);
            request.setDryCleanerId(10L);

            assertThatThrownBy(() -> orderService.rejectOrder(request))
                    .isInstanceOf(InvalidOrderAssignmentException.class)
                    .hasMessageContaining("cannot be rejected");
        }

        @Test
        @DisplayName("should throw OrdersNotFoundException when order does not exist")
        void shouldThrowWhenOrderNotFound() {
            when(orderEntityRepository.findById(99L)).thenReturn(Optional.empty());

            RejectOrderRequest request = new RejectOrderRequest();
            request.setOrderId(99L);
            request.setDryCleanerId(10L);

            assertThatThrownBy(() -> orderService.rejectOrder(request))
                    .isInstanceOf(OrdersNotFoundException.class);
        }
    }

   
    // updateOrderStatus
   
    @Nested
    @DisplayName("updateOrderStatus()")
    class UpdateOrderStatus {

        @Test
        @DisplayName("should transition ACCEPTED → SCHEDULED successfully")
        void shouldTransitionAcceptedToScheduled() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            OrderEntity order = buildPendingOrder(customer, dryCleaner);
            order.setOrderStatus(OrderStatus.ACCEPTED);

            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderEntityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
            request.setOrderId(1L);
            request.setStatus(OrderStatus.SCHEDULED);

            UpdateOrderStatusResponse response = orderService.updateOrderStatus(request);

            assertThat(response.getStatus()).isEqualTo(OrderStatus.SCHEDULED);
            assertThat(response.getOrderId()).isEqualTo(1L);
            assertThat(response.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should throw InvalidOrderAssignmentException for illegal transition")
        void shouldThrowForIllegalTransition() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            OrderEntity order = buildPendingOrder(customer, dryCleaner);
            order.setOrderStatus(OrderStatus.DELIVERED);

            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));

            UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
            request.setOrderId(1L);
            request.setStatus(OrderStatus.PENDING_ACCEPTANCE); // illegal backwards transition

            assertThatThrownBy(() -> orderService.updateOrderStatus(request))
                    .isInstanceOf(InvalidOrderAssignmentException.class)
                    .hasMessageContaining("Transition not allowed");
        }

        @Test
        @DisplayName("should not allow transition from terminal COMPLETED state")
        void shouldNotAllowTransitionFromCompleted() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            OrderEntity order = buildPendingOrder(customer, dryCleaner);
            order.setOrderStatus(OrderStatus.COMPLETED);

            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));

            UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
            request.setOrderId(1L);
            request.setStatus(OrderStatus.DELIVERED);

            assertThatThrownBy(() -> orderService.updateOrderStatus(request))
                    .isInstanceOf(InvalidOrderAssignmentException.class);
        }

        @Test
        @DisplayName("should throw OrdersNotFoundException when order does not exist")
        void shouldThrowWhenOrderNotFound() {
            when(orderEntityRepository.findById(99L)).thenReturn(Optional.empty());

            UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
            request.setOrderId(99L);
            request.setStatus(OrderStatus.ACCEPTED);

            assertThatThrownBy(() -> orderService.updateOrderStatus(request))
                    .isInstanceOf(OrdersNotFoundException.class);
        }
    }

   
    // getOrderById
   
    @Nested
    @DisplayName("getOrderById()")
    class GetOrderById {

        @Test
        @DisplayName("should return order details for existing order")
        void shouldReturnOrderDetails() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            OrderEntity order = buildPendingOrder(customer, dryCleaner);

            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));

            OrderDetailsResponse response = orderService.getOrderById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getCleanerName()).isEqualTo("Sparkle Cleaners");
            assertThat(response.getCustomerName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("should throw OrdersNotFoundException for unknown orderId")
        void shouldThrowWhenOrderNotFound() {
            when(orderEntityRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.getOrderById(99L))
                    .isInstanceOf(OrdersNotFoundException.class);
        }
    }

   
    // getOrdersByCustomerId
   
    @Nested
    @DisplayName("getOrdersByCustomerId()")
    class GetOrdersByCustomer {

        @Test
        @DisplayName("should return all orders for given customer ID")
        void shouldReturnCustomerOrders() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            OrderEntity order1 = buildPendingOrder(customer, dryCleaner);
            OrderEntity order2 = buildPendingOrder(customer, dryCleaner);
            order2.setId(2L);

            when(orderEntityRepository.findByCustomerIdOrderByCreatedAtDesc(1L))
                    .thenReturn(List.of(order1, order2));

            List<OrderDetailsResponse> results = orderService.getOrdersByCustomerId(1L);

            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("should return empty list when customer has no orders")
        void shouldReturnEmptyListForCustomerWithNoOrders() {
            when(orderEntityRepository.findByCustomerIdOrderByCreatedAtDesc(1L))
                    .thenReturn(new ArrayList<>());

            List<OrderDetailsResponse> results = orderService.getOrdersByCustomerId(1L);

            assertThat(results).isEmpty();
        }
    }

    
    // getOrdersByDryCleanerId
   
    @Nested
    @DisplayName("getOrdersByDryCleanerId()")
    class GetOrdersByDryCleaner {

        @Test
        @DisplayName("should return all orders for given dry cleaner ID")
        void shouldReturnDryCleanerOrders() {
            CustomerEntity customer = buildCustomer();
            DryCleanerEntity dryCleaner = buildDryCleaner();
            OrderEntity order = buildPendingOrder(customer, dryCleaner);

            when(orderEntityRepository.findByDryCleanerIdOrderByCreatedAtDesc(10L))
                    .thenReturn(List.of(order));

            List<OrderDetailsResponse> results = orderService.getOrdersByDryCleanerId(10L);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getCleanerName()).isEqualTo("Sparkle Cleaners");
        }
    }
}
