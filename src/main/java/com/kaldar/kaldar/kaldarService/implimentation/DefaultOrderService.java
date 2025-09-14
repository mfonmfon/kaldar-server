package com.kaldar.kaldar.kaldarService.implimentation;

import com.kaldar.kaldar.contants.OrderStatus;
import com.kaldar.kaldar.domain.entities.*;
import com.kaldar.kaldar.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.domain.repository.OrderEntityRepository;
import com.kaldar.kaldar.domain.repository.ServiceOfferingRepository;
import com.kaldar.kaldar.dtos.request.CreateOrderRequest;
import com.kaldar.kaldar.dtos.request.OrderItemsRequest;
import com.kaldar.kaldar.dtos.response.CreateOrderResponse;
import com.kaldar.kaldar.dtos.response.OrderLineResponse;
import com.kaldar.kaldar.exceptions.NoItemsFoundException;
import com.kaldar.kaldar.exceptions.UserNotFoundException;
import com.kaldar.kaldar.kaldarService.interfaces.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.kaldar.kaldar.contants.StatusResponse.*;

@Service
public class DefaultOrderService implements OrderService {
    private final CustomerEntityRepository customerEntityRepository;
    private final DryCleanerEntityRepository dryCleanerEntityRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final OrderEntityRepository orderEntityRepository;

    public DefaultOrderService(CustomerEntityRepository customerEntityRepository, DryCleanerEntityRepository dryCleanerEntityRepository, ServiceOfferingRepository serviceOfferingRepository, OrderEntityRepository orderEntityRepository) {
        this.customerEntityRepository = customerEntityRepository;
        this.dryCleanerEntityRepository = dryCleanerEntityRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.orderEntityRepository = orderEntityRepository;
    }

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        CustomerEntity customer = customerEntityRepository.findById(createOrderRequest.getCustomerId())
                .orElseThrow(()-> new UserNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        DryCleanerEntity dryCleaner = dryCleanerEntityRepository.findById(createOrderRequest.getDryCleanerId())
                .orElseThrow(()-> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        OrderEntity order = new OrderEntity();
        order.setCustomer(customer);
        order.setDryCleaner(dryCleaner);
        order.setPickupAddress(createOrderRequest.getPickupAddress());
        order.setDeliveryAddress(createOrderRequest.getDeliveryAddress());
        order.setOrderStatus(OrderStatus.PENDING_ACCEPTANCE);
        order.setCreatedAt(LocalDateTime.now());
        double totalAmount = 0.0;
       List<OrderLine> orderLines = new ArrayList<>();
        totalAmount = buildOrderTotalAmount(createOrderRequest, totalAmount, order, orderLines);
        order.setOrderLines(orderLines);
       order.setTotalAmount(totalAmount);
       OrderEntity savedOrderEntity = orderEntityRepository.save(order);
        return mapOrderToResponse(savedOrderEntity);
    }

    private double buildOrderTotalAmount(CreateOrderRequest createOrderRequest, double totalAmount, OrderEntity order, List<OrderLine> orderLines) {
        for (OrderItemsRequest orderItemsRequest : createOrderRequest.getOrderItemsRequests()){
            ServiceOfferings serviceOfferings = serviceOfferingRepository.findById(orderItemsRequest.getServiceOfferingId())
                    .orElseThrow(()-> new NoItemsFoundException(NO_ITEMS_FOUND_EXCEPTION_MESSAGE.getMessage()));
            double lineTotal = serviceOfferings.getUnitPrice() * orderItemsRequest.getQuantity();
            totalAmount += lineTotal;
            OrderLine orderLine = new OrderLine();
            orderLine.setOrder(order);
            orderLine.setQuantity(orderItemsRequest.getQuantity());
            orderLine.setLineTotal(lineTotal);
             orderLines.add(orderLine);
        }
        return totalAmount;
    }

    private CreateOrderResponse mapOrderToResponse(OrderEntity orderEntity){
        CreateOrderResponse createOrderResponse = new CreateOrderResponse();
        createOrderResponse.setOrderId(orderEntity.getId());
        createOrderResponse.setCustomerId(orderEntity.getCustomer().getId());
        createOrderResponse.setDryCleanerId(orderEntity.getDryCleaner().getId());
        createOrderResponse.setPickupAddress(orderEntity.getPickupAddress());
        createOrderResponse.setDeliveryAddress(orderEntity.getDeliveryAddress());
        createOrderResponse.setStatus(orderEntity.getOrderStatus().name());
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
        return createOrderResponse;

    }
}
