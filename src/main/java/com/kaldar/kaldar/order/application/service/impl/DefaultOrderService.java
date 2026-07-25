package com.kaldar.kaldar.order.application.service.impl;

import com.kaldar.kaldar.shared.domain.constants.OrderStatus;
import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.customer.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import com.kaldar.kaldar.drycleaner.domain.model.ServiceOffering;
import com.kaldar.kaldar.drycleaner.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.drycleaner.domain.repository.ServiceOfferingRepository;
import com.kaldar.kaldar.order.domain.model.OrderEntity;
import com.kaldar.kaldar.order.domain.model.OrderServiceItem;
import com.kaldar.kaldar.order.domain.repository.OrderEntityRepository;
import com.kaldar.kaldar.order.domain.repository.OrderServiceItemRepository;
import com.kaldar.kaldar.order.application.dto.request.AcceptOrderRequest;
import com.kaldar.kaldar.order.application.dto.response.AcceptOrderResponse;
import com.kaldar.kaldar.order.application.dto.request.CreateOrderRequest;
import com.kaldar.kaldar.order.application.dto.request.OrderItemRequest;
import com.kaldar.kaldar.order.application.dto.request.UpdateOrderStatusRequest;
import com.kaldar.kaldar.order.application.dto.response.CreateOrderResponse;
import com.kaldar.kaldar.order.application.dto.response.*;
import com.kaldar.kaldar.shared.domain.exceptions.*;
import com.kaldar.kaldar.order.application.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.*;

@Service
public class DefaultOrderService implements OrderService {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderStatus.class);
    static {
        ALLOWED_TRANSITIONS.put(OrderStatus.PENDING_ACCEPTANCE,
                Set.of(OrderStatus.ACCEPTED, OrderStatus.REJECTED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.ACCEPTED, Set.of(OrderStatus.SCHEDULED));
        ALLOWED_TRANSITIONS.put(OrderStatus.SCHEDULED, Set.of(OrderStatus.PICKED, OrderStatus.PICKED_UP));
        ALLOWED_TRANSITIONS.put(OrderStatus.PICKED, Set.of(OrderStatus.CLEANING));
        ALLOWED_TRANSITIONS.put(OrderStatus.PICKED_UP, Set.of(OrderStatus.CLEANING));
        ALLOWED_TRANSITIONS.put(OrderStatus.CLEANING, Set.of(OrderStatus.READY, OrderStatus.READY_FOR_DELIVERY));
        ALLOWED_TRANSITIONS.put(OrderStatus.READY, Set.of(OrderStatus.OUT_FOR_DELIVERY));
        ALLOWED_TRANSITIONS.put(OrderStatus.READY_FOR_DELIVERY, Set.of(OrderStatus.OUT_FOR_DELIVERY));
        ALLOWED_TRANSITIONS.put(OrderStatus.OUT_FOR_DELIVERY, Set.of(OrderStatus.DELIVERED, OrderStatus.COMPLETED));
        ALLOWED_TRANSITIONS.put(OrderStatus.DELIVERED, Set.of(OrderStatus.COMPLETED));
        // Terminal states have no outgoing transitions
        ALLOWED_TRANSITIONS.put(OrderStatus.REJECTED, Set.of());
        ALLOWED_TRANSITIONS.put(OrderStatus.CANCELLED, Set.of());
        ALLOWED_TRANSITIONS.put(OrderStatus.COMPLETED, Set.of());
        ALLOWED_TRANSITIONS.put(OrderStatus.CREATED, Set.of(OrderStatus.PENDING_ACCEPTANCE));
    }

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final CustomerEntityRepository customerEntityRepository;
    private final DryCleanerEntityRepository dryCleanerEntityRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final OrderEntityRepository orderEntityRepository;
    private final OrderServiceItemRepository orderServiceItemRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final com.kaldar.kaldar.order.domain.repository.ReviewRepository reviewRepository;

    public DefaultOrderService(CustomerEntityRepository customerEntityRepository,
            DryCleanerEntityRepository dryCleanerEntityRepository,
            ServiceOfferingRepository serviceOfferingRepository,
            OrderEntityRepository orderEntityRepository, OrderServiceItemRepository orderServiceItemRepository,
            ApplicationEventPublisher applicationEventPublisher,
            com.kaldar.kaldar.order.domain.repository.ReviewRepository reviewRepository) {
        this.customerEntityRepository = customerEntityRepository;
        this.dryCleanerEntityRepository = dryCleanerEntityRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.orderEntityRepository = orderEntityRepository;
        this.orderServiceItemRepository = orderServiceItemRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    @Override
    public AcceptOrderResponse acceptOrder(AcceptOrderRequest acceptOrderRequest) {
        DryCleanerEntity dryCleaner = dryCleanerEntityRepository.findById(acceptOrderRequest.getDryCleanerId())
                .orElseThrow(() -> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        OrderEntity order = orderEntityRepository.findById(acceptOrderRequest.getOrderId())
                .orElseThrow(() -> new OrdersNotFoundException(ORDERS_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        if (order.getDryCleaner() == null || !order.getDryCleaner().getId().equals(dryCleaner.getId())) {
            throw new InvalidOrderAssignmentException("Order not assigned to this drycleaner");
        }
        if (order.getOrderStatus() != OrderStatus.PENDING_ACCEPTANCE)
            throw new InvalidOrderAssignmentException("Order cannot be accepted");
        if (!dryCleaner.isActive())
            throw new NoActiveDryCleanerException(dryCleaner.getId());
        List<String> missingService = findMissingService(order, dryCleaner);
        if (!missingService.isEmpty()) {
            throw new MissingServicesNotEmptyException("Missing Service" + String.join(" ", missingService));
        }
        if (acceptOrderRequest.getPickupAt() != null) {
            order.setPickupAt(acceptOrderRequest.getPickupAt());
        } else if (order.getPickupAt() == null) {
            order.setPickupAt(LocalDateTime.now().plusHours(24));
        }
        order.setOrderStatus(OrderStatus.ACCEPTED);
        OrderEntity orderEntity = orderEntityRepository.save(order);
        applicationEventPublisher
                .publishEvent(new com.kaldar.kaldar.order.application.event.OrderAcceptedEvent(this, order.getId()));
        AcceptOrderResponse acceptOrderResponse = new AcceptOrderResponse();
        acceptOrderResponse.setOrderId(order.getId());
        acceptOrderResponse.setStatus("ACCEPTED");
        acceptOrderResponse.setTimestamp(LocalDateTime.now());
        return acceptOrderResponse;
    }

    @Override
    @Transactional
    public CreateOrderResponse placeOrder(CreateOrderRequest createOrderRequest) {
        validateCreateOrderRequest(createOrderRequest);
        CustomerEntity customer = customerEntityRepository.findById(createOrderRequest.getCustomerId())
                .orElseThrow(() -> new UserNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        DryCleanerEntity dryCleaner = resolveDryCleaner(createOrderRequest.getDryCleanerId());
        OrderEntity order = buildOrder(createOrderRequest, customer, dryCleaner);
        List<OrderServiceItem> orderItems = buildOrderServiceItems(createOrderRequest.getServiceItems(), order);
        order.setOrderServiceItems(orderItems);
        order.setTotalAmount(calculateTotalAmount(orderItems));
        OrderEntity savedOrder = orderEntityRepository.save(order);
        return mapToCreateOrderResponse(savedOrder);
    }

    @Transactional
    @Override
    public UpdateOrderStatusResponse updateOrderStatus(UpdateOrderStatusRequest request) {
        OrderEntity order = orderEntityRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrdersNotFoundException(ORDERS_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        OrderStatus from = order.getOrderStatus();
        OrderStatus to = request.getStatus();
        if (from == null) {
            // initialize if missing
            from = OrderStatus.CREATED;
        }
        if (!isTransitionAllowed(from, to)) {
            throw new InvalidOrderAssignmentException("Transition not allowed: " + from + " -> " + to);
        }
        order.setOrderStatus(to);
        order.setUpdatedAt(LocalDateTime.now());
        orderEntityRepository.save(order);
        UpdateOrderStatusResponse resp = new UpdateOrderStatusResponse();
        resp.setOrderId(order.getId());
        resp.setStatus(order.getOrderStatus());
        resp.setUpdatedAt(LocalDateTime.now());
        return resp;
    }

    private boolean isTransitionAllowed(OrderStatus from, OrderStatus to) {
        Set<OrderStatus> next = ALLOWED_TRANSITIONS.getOrDefault(from, Collections.emptySet());
        return next.contains(to);
    }

    private List<String> findMissingService(OrderEntity order, DryCleanerEntity dryCleaner) {
        List<OrderServiceItem> items = order.getOrderServiceItems();
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        List<ServiceOffering> offerings = dryCleaner.getServiceOfferings();
        if (offerings == null || offerings.isEmpty()) {
            return List.of("No service offerings configured");
        }
        Set<Long> offeringIds = offerings.stream()
                .map(ServiceOffering::getId)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());
        Set<String> missing = new LinkedHashSet<>();
        for (OrderServiceItem item : items) {
            ServiceOffering offering = item.getServiceOffering();
            if (offering == null || offering.getId() == null) {
                missing.add("Unknown service");
                continue;
            }
            if (!offeringIds.contains(offering.getId())) {
                String name = offering.getServiceName();
                missing.add(name == null ? "Service#" + offering.getId() : name);
            }
        }
        return new ArrayList<>(missing);
    }

    private static void validateCreateOrderRequest(CreateOrderRequest request) {
        if (request == null) {
            throw new EmptyRequiredFieldException("Order request is required");
        }
        if (request.getCustomerId() == null) {
            throw new EmptyRequiredFieldException("Customer id is required");
        }
    }

    private DryCleanerEntity resolveDryCleaner(Long dryCleanerId) {
        if (dryCleanerId == null) {
            return null;
        }
        return dryCleanerEntityRepository.findById(dryCleanerId)
                .orElseThrow(() -> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
    }

    private static OrderEntity buildOrder(CreateOrderRequest request, CustomerEntity customer,
            DryCleanerEntity dryCleaner) {
        OrderEntity order = new OrderEntity();
        order.setCustomer(customer);
        if (dryCleaner != null) {
            order.setDryCleaner(dryCleaner);
        }
        order.setPickupAddress(request.getPickupAddress());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setWashingPreference(request.getWashingPreference());
        order.setOrderStatus(OrderStatus.PENDING_ACCEPTANCE);
        LocalDateTime createdAt = request.getCreatedAt() == null ? LocalDateTime.now() : request.getCreatedAt();
        order.setCreatedAt(createdAt);
        order.setUpdatedAt(createdAt);
        return order;
    }

    private List<OrderServiceItem> buildOrderServiceItems(List<OrderItemRequest> requestItems, OrderEntity order) {
        if (requestItems == null || requestItems.isEmpty()) {
            return new ArrayList<>();
        }
        List<OrderServiceItem> items = new ArrayList<>(requestItems.size());
        for (OrderItemRequest requestItem : requestItems) {
            if (requestItem.getQuantity() <= 0) {
                throw new EmptyRequiredFieldException("Item quantity must be greater than zero");
            }
            if (requestItem.getServiceOfferingId() == null) {
                throw new EmptyRequiredFieldException("Service offering id is required");
            }
            ServiceOffering serviceOffering = serviceOfferingRepository.findById(requestItem.getServiceOfferingId())
                    .orElseThrow(() -> new ServicesNotFoundException("Service not found"));
            if (order.getDryCleaner() != null && serviceOffering.getDryCleaner() != null) {
                if (!order.getDryCleaner().getId().equals(serviceOffering.getDryCleaner().getId())) {
                    throw new InvalidOrderAssignmentException("Service does not belong to selected dry cleaner");
                }
            }
            OrderServiceItem item = new OrderServiceItem();
            item.setOrder(order);
            item.setServiceOffering(serviceOffering);
            item.setPriceSnapshot(serviceOffering.getUnitPrice());
            item.setQuantity(requestItem.getQuantity());
            item.setCreatedAt(LocalDateTime.now());
            items.add(item);
        }
        return items;
    }

    private static double calculateTotalAmount(List<OrderServiceItem> items) {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (OrderServiceItem item : items) {
            double unitPrice = item.getPriceSnapshot() == null ? 0.0 : item.getPriceSnapshot();
            total += unitPrice * item.getQuantity();
        }
        return total;
    }

    private static CreateOrderResponse mapToCreateOrderResponse(OrderEntity order) {
        CreateOrderResponse createOrderResponse = new CreateOrderResponse();
        createOrderResponse.setOrderId(order.getId());
        createOrderResponse.setCustomerId(order.getCustomer().getId());
        if (order.getDryCleaner() != null) {
            createOrderResponse.setDryCleanerId(order.getDryCleaner().getId());
        }
        createOrderResponse.setPickupAddress(order.getPickupAddress());
        createOrderResponse.setDeliveryAddress(order.getDeliveryAddress());
        createOrderResponse.setTotalPrice(order.getTotalAmount() == null ? 0.0 : order.getTotalAmount());
        createOrderResponse.setCreatedAt(order.getCreatedAt());
        createOrderResponse.setStatus(ORDER_CREATED_SUCCESS_MESSAGE.getMessage());
        return createOrderResponse;
    }

    @Override
    public OrderDetailsResponse getOrderById(Long orderId) {
        OrderEntity order = orderEntityRepository.findById(orderId)
                .orElseThrow(() -> new OrdersNotFoundException(ORDERS_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        return mapToOrderDetailsResponse(order);
    }

    @Override
    public List<OrderDetailsResponse> getOrdersByCustomerId(Long customerId) {
        List<OrderEntity> orders = orderEntityRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        return orders.stream().map(this::mapToOrderDetailsResponse).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<OrderDetailsResponse> getOrdersByDryCleanerId(Long dryCleanerId) {
        List<OrderEntity> orders = orderEntityRepository.findByDryCleanerIdOrderByCreatedAtDesc(dryCleanerId);
        return orders.stream().map(this::mapToOrderDetailsResponse).collect(java.util.stream.Collectors.toList());
    }

    private OrderDetailsResponse mapToOrderDetailsResponse(OrderEntity order) {
        OrderDetailsResponse resp = new OrderDetailsResponse();
        resp.setId(order.getId());
        resp.setOrderNumber("ORD-" + order.getId()); // Simple fallback
        resp.setStatus(order.getOrderStatus().name().toLowerCase());
        resp.setCreatedAt(order.getCreatedAt());
        resp.setPickupAddress(order.getPickupAddress());
        resp.setDeliveryAddress(order.getDeliveryAddress());
        resp.setPickupTime(order.getPickupAt());
        resp.setDeliveryTime(order.getDeliveryAt());
        resp.setTotalCost(order.getTotalAmount() != null ? order.getTotalAmount() : 0.0);

        if (order.getDryCleaner() != null) {
            resp.setCleanerName(order.getDryCleaner().getBusinessName());
        }

        if (order.getCustomer() != null) {
            resp.setCustomerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());
        }

        if (order.getOrderServiceItems() != null) {
            List<OrderItemResponse> itemResponses = order.getOrderServiceItems().stream().map(item -> {
                OrderItemResponse ir = new OrderItemResponse();
                if (item.getServiceOffering() != null) {
                    ir.setClothType(item.getServiceOffering().getServiceName());
                }
                ir.setQuantity(item.getQuantity());
                ir.setPricePerItem(item.getPriceSnapshot() != null ? item.getPriceSnapshot() : 0.0);
                ir.setSubtotal(ir.getPricePerItem() * ir.getQuantity());
                return ir;
            }).collect(java.util.stream.Collectors.toList());
            resp.setItems(itemResponses);
        }

        return resp;
    }

    @Override
    @Transactional
    public com.kaldar.kaldar.order.application.dto.response.SubmitReviewResponse submitReview(
            com.kaldar.kaldar.order.application.dto.request.SubmitReviewRequest request) {
        OrderEntity order = orderEntityRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrdersNotFoundException(ORDERS_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));

        CustomerEntity customer = customerEntityRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new UserNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));

        DryCleanerEntity dryCleaner = dryCleanerEntityRepository.findById(request.getDryCleanerId())
                .orElseThrow(() -> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));

        com.kaldar.kaldar.order.domain.model.ReviewEntity review = new com.kaldar.kaldar.order.domain.model.ReviewEntity();
        review.setOrder(order);
        review.setCustomer(customer);
        review.setDryCleaner(dryCleaner);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        com.kaldar.kaldar.order.domain.model.ReviewEntity saved = reviewRepository.save(review);

        return new com.kaldar.kaldar.order.application.dto.response.SubmitReviewResponse(saved.getId(),
                "Review submitted successfully");
    }

    @Override
    @Transactional
    public com.kaldar.kaldar.order.application.dto.response.RejectOrderResponse rejectOrder(
            com.kaldar.kaldar.order.application.dto.request.RejectOrderRequest request) {
        OrderEntity order = orderEntityRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrdersNotFoundException(ORDERS_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));

        DryCleanerEntity dryCleaner = dryCleanerEntityRepository.findById(request.getDryCleanerId())
                .orElseThrow(() -> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));

        if (order.getDryCleaner() == null || !order.getDryCleaner().getId().equals(dryCleaner.getId())) {
            throw new InvalidOrderAssignmentException("Order not assigned to this drycleaner");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING_ACCEPTANCE) {
            throw new InvalidOrderAssignmentException(
                    "Order cannot be rejected from current state: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.REJECTED);
        order.setRejectionReason(request.getReason());
        order.setUpdatedAt(LocalDateTime.now());
        orderEntityRepository.save(order);

        return new com.kaldar.kaldar.order.application.dto.response.RejectOrderResponse(order.getId(), "REJECTED",
                LocalDateTime.now());
    }
}
