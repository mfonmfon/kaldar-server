package com.kaldar.kaldar.kaldarService.implimentation;

import com.kaldar.kaldar.contants.OrderStatus;
import com.kaldar.kaldar.domain.entities.*;
import com.kaldar.kaldar.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.domain.repository.OrderEntityRepository;
import com.kaldar.kaldar.domain.repository.ServiceOfferingRepository;
import com.kaldar.kaldar.dtos.request.AcceptOrderRequest;
import com.kaldar.kaldar.dtos.request.AcceptOrderResponse;
import com.kaldar.kaldar.dtos.request.CreateOrderRequest;
import com.kaldar.kaldar.dtos.request.OrderItemsRequest;
import com.kaldar.kaldar.dtos.response.CreateOrderResponse;
import com.kaldar.kaldar.dtos.response.OrderLineResponse;
import com.kaldar.kaldar.exceptions.*;
import com.kaldar.kaldar.kaldarService.interfaces.OrderService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kaldar.kaldar.contants.StatusResponse.*;

@Service
public class DefaultOrderService implements OrderService {

    private final CustomerEntityRepository customerEntityRepository;
    private final DryCleanerEntityRepository dryCleanerEntityRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final OrderEntityRepository orderEntityRepository;

    public DefaultOrderService(CustomerEntityRepository customerEntityRepository,
                               DryCleanerEntityRepository dryCleanerEntityRepository,
                               ServiceOfferingRepository serviceOfferingRepository,
                               OrderEntityRepository orderEntityRepository) {
        this.customerEntityRepository = customerEntityRepository;
        this.dryCleanerEntityRepository = dryCleanerEntityRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.orderEntityRepository = orderEntityRepository;
    }

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        CustomerEntity customer = customerEntityRepository.findById(createOrderRequest.getCustomerId())
                .orElseThrow(() -> new UserNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        DryCleanerEntity dryCleaner = dryCleanerEntityRepository.findById(createOrderRequest.getDryCleanerId())
                .orElseThrow(() -> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        OrderEntity order = mapOrderRequestInstance(createOrderRequest, customer, dryCleaner);
        double totalAmount = 0.0;
        List<OrderLine> orderLines = new ArrayList<>();
        totalAmount = buildOrderTotalAmount(createOrderRequest, totalAmount, order, orderLines);
        order.setOrderLines(orderLines);
        order.setTotalAmount(totalAmount);
        OrderEntity savedOrderEntity = orderEntityRepository.save(order);
        return mapOrderToResponse(savedOrderEntity);
    }

    private static @NotNull OrderEntity mapOrderRequestInstance(CreateOrderRequest createOrderRequest, CustomerEntity customer,DryCleanerEntity dryCleaner) {
        OrderEntity order = new OrderEntity();
        order.setCustomer(customer);
        order.setDryCleaner(dryCleaner);
        order.setPickupAddress(createOrderRequest.getPickupAddress());
        order.setDeliveryAddress(createOrderRequest.getDeliveryAddress());
        order.setOrderStatus(OrderStatus.PENDING_ACCEPTANCE);
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }

    private double buildOrderTotalAmount(CreateOrderRequest createOrderRequest, double totalAmount, OrderEntity order, List<OrderLine> orderLines) {
        for (OrderItemsRequest orderItemsRequest : createOrderRequest.getOrderItemsRequests()) {
            ServiceOfferings serviceOfferings = serviceOfferingRepository.findById(orderItemsRequest.getServiceOfferingId())
                    .orElseThrow(() -> new NoItemsFoundException(NO_ITEMS_FOUND_EXCEPTION_MESSAGE.getMessage()));
            double lineTotal = serviceOfferings.getUnitPrice() * orderItemsRequest.getQuantity();
            totalAmount += lineTotal;
            OrderLine orderLine = new OrderLine();
            orderLine.setOrder(order);
            orderLine.setServiceOfferings(serviceOfferings);
            orderLine.setQuantity(orderItemsRequest.getQuantity());
            orderLine.setLineTotal(lineTotal);
            orderLines.add(orderLine);
        }
        return totalAmount;
    }

    private CreateOrderResponse mapOrderToResponse(OrderEntity orderEntity) {
        CreateOrderResponse createOrderResponse = new CreateOrderResponse();
        createOrderResponse.setOrderId(orderEntity.getId());
        createOrderResponse.setCustomerId(orderEntity.getCustomer().getId());
        createOrderResponse.setDryCleanerId(orderEntity.getDryCleaner().getId());
        createOrderResponse.setPickupAddress(orderEntity.getPickupAddress());
        createOrderResponse.setDeliveryAddress(orderEntity.getDeliveryAddress());
        createOrderResponse.setTotalPrice(orderEntity.getTotalAmount());
        List<OrderLineResponse> orderLineResponses = orderEntity.getOrderLines().stream()
                .map(line -> {
                    OrderLineResponse orderLineResponse = new OrderLineResponse();
                    orderLineResponse.setServiceOfferingId(line.getServiceOfferings().getId());
                    orderLineResponse.setQuantity(line.getQuantity());
                    orderLineResponse.setServiceName(line.getServiceOfferings().getName());
                    orderLineResponse.setLineTotal(line.getLineTotal());
                    return orderLineResponse;
                }).toList();
        createOrderResponse.setOrderLines(orderLineResponses);
        createOrderResponse.setStatus(ORDER_CREATED_SUCCESS_MESSAGE.getMessage());
        return createOrderResponse;
    }
    @Override
    public AcceptOrderResponse acceptOrder(AcceptOrderRequest acceptOrderRequest) {
        OrderEntity orders = orderEntityRepository.findById(acceptOrderRequest.getOrderId())
                .orElseThrow(()-> new OrdersNotFoundException(ORDERS_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        DryCleanerEntity dryCleanerEntity = dryCleanerEntityRepository.findById(acceptOrderRequest.getDryCleanerId())
                .orElseThrow(()-> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        //check if the order is assign to the drycleaner
        validateDryCleanerAssignment(orders, dryCleanerEntity);
        //check for order status
        validateOrderStatus(orders);
        validateDryCleanerStatus(dryCleanerEntity);
        orders.setDryCleaner(dryCleanerEntity);
        orders.setOrderStatus(OrderStatus.SCHEDULED);
        if (orders.getPickupAt() == null){
            orders.setPickupAt(LocalDateTime.now().plusHours(24));
        }
        validateMissingService(orders, dryCleanerEntity);
        orderEntityRepository.save(orders);
        return mapAcceptOrderResponse(orders);
    }

    private static @NotNull AcceptOrderResponse mapAcceptOrderResponse(OrderEntity orders) {
        AcceptOrderResponse acceptOrderResponse = new AcceptOrderResponse();
        acceptOrderResponse.setOrderId(orders.getId());
        acceptOrderResponse.setStatus(ACCEPT_ORDER_SUCCESS_MESSAGE.getMessage());
        acceptOrderResponse.setTimestamp(LocalDateTime.now());
        return acceptOrderResponse;
    }

    private void validateMissingService(OrderEntity orders, DryCleanerEntity dryCleanerEntity) {
        List<String> missingService = findMissingService(orders, dryCleanerEntity);
        if (!missingService.isEmpty())
            throw new MissingServicesNotEmptyException(missingService);
    }

    private void validateDryCleanerStatus(DryCleanerEntity dryCleanerEntity) {
        if (Boolean.FALSE.equals(dryCleanerEntity.isActive())){
            throw new NoActiveDryCleanerException(dryCleanerEntity.getId());
        }
    }

    private void validateOrderStatus(OrderEntity orders) {
        if (orders.getOrderStatus() != OrderStatus.ACCEPTED && orders.getOrderStatus() != OrderStatus.PENDING_ACCEPTANCE){
            throw new InvalidOrderAssignmentException("Order cannot be accepted in it's current status"+ orders.getOrderStatus());
        }
    }

    private void validateDryCleanerAssignment(OrderEntity orders, DryCleanerEntity dryCleanerEntity) {
        if (orders.getDryCleaner() == null || !orders.getDryCleaner().getId().equals(dryCleanerEntity.getId())){
            throw new InvalidOrderAssignmentException("Order is not assign to this drycleaner");
        }
    }

    private List<String> findMissingService(OrderEntity orders, DryCleanerEntity dryCleanerEntity) {
        Set<String> availableService = dryCleanerEntity.getServiceOfferings() == null ? Collections.emptySet()
                : dryCleanerEntity.getServiceOfferings().stream().map(ServiceOfferings::getName).collect(Collectors.toSet());
        return orders.getOrderLines() == null ? Collections.emptyList()
                : orders.getOrderLines().stream().map(OrderLine::getClothType)
                .filter(line -> !availableService.contains(line))
                .distinct()
                .collect(Collectors.toList());
    }

}
