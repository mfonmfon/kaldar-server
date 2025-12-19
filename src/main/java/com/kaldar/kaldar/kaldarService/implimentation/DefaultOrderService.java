package com.kaldar.kaldar.kaldarService.implimentation;
import com.kaldar.kaldar.contants.OrderStatus;
import com.kaldar.kaldar.customermdoule.domain.model.CustomerEntity;
import com.kaldar.kaldar.customermdoule.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.domain.entities.*;
import com.kaldar.kaldar.domain.repository.*;
import com.kaldar.kaldar.dtos.request.AcceptOrderRequest;
import com.kaldar.kaldar.dtos.request.AcceptOrderResponse;
import com.kaldar.kaldar.dtos.request.CreateOrderRequest;
import com.kaldar.kaldar.dtos.request.OrderItemsDTO;
import com.kaldar.kaldar.dtos.response.CreateOrderResponse;
import com.kaldar.kaldar.dtos.response.OrderTotalSummaryResponse;
import com.kaldar.kaldar.exceptions.*;
import com.kaldar.kaldar.kaldarService.interfaces.OrderService;
import com.kaldar.kaldar.kaldarService.interfaces.PricingService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static com.kaldar.kaldar.contants.StatusResponse.*;


@Service
public class  DefaultOrderService implements OrderService {


    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final CustomerEntityRepository customerEntityRepository;
    private final DryCleanerEntityRepository dryCleanerEntityRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final OrderEntityRepository orderEntityRepository;
    private final OrderItemRepository orderItemRepository;
    private final PricingService pricingService;

    public DefaultOrderService(CustomerEntityRepository customerEntityRepository,
                               DryCleanerEntityRepository dryCleanerEntityRepository,
                               ServiceOfferingRepository serviceOfferingRepository,
                               OrderEntityRepository orderEntityRepository, OrderItemRepository orderItemRepository, PricingService pricingService) {
        this.customerEntityRepository = customerEntityRepository;
        this.dryCleanerEntityRepository = dryCleanerEntityRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.orderEntityRepository = orderEntityRepository;
        this.orderItemRepository = orderItemRepository;

        this.pricingService = pricingService;
    }

    @Transactional
    @Override
    public AcceptOrderResponse acceptOrder(AcceptOrderRequest acceptOrderRequest) {
        DryCleanerEntity dryCleaner = dryCleanerEntityRepository.findById(acceptOrderRequest.getDryCleanerId())
                .orElseThrow(()-> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        OrderEntity order = orderEntityRepository.findById(acceptOrderRequest.getOrderId())
                .orElseThrow(()-> new OrdersNotFoundException(ORDERS_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        if (order.getDryCleaner() == null || !order.getDryCleaner().getId().equals(dryCleaner.getId())){
            throw new InvalidOrderAssignmentException("Order not assigned to this drycleaner");
        }
        if (order.getOrderStatus() != OrderStatus.PENDING)
            throw new InvalidOrderAssignmentException("Order cannot be accepted");

        if (Boolean.FALSE.equals(dryCleaner.isActive()))
            throw new NoActiveDryCleanerException(dryCleaner.getId());

        List<String> missingService = findMissingService(order,dryCleaner);
        if (missingService.isEmpty()){
            throw new MissingServicesNotEmptyException("Missing Service" + String.join(" ", missingService));
        }
        if (order.getPickupAt() == null)
            order.setOrderStatus(OrderStatus.ACCEPTED);
        order.setPickupAt(LocalDateTime.now().plusHours(24));
       OrderEntity orderEntity = orderEntityRepository.save(order);
    //    log.info("order entity saved " orderEntity)
        AcceptOrderResponse acceptOrderResponse = new AcceptOrderResponse();
        acceptOrderResponse.setOrderId(order.getId());
        acceptOrderResponse.setStatus("ACCEPTED");
        acceptOrderResponse.setTimestamp(LocalDateTime.now());
        return acceptOrderResponse;
    }

    @Override
    public CreateOrderResponse placeOrder(CreateOrderRequest createOrderRequest) {
        CustomerEntity customer = customerEntityRepository.findById(createOrderRequest.getCustomerId())
                .orElseThrow(()-> new UserNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        DryCleanerEntity dryCleaner = dryCleanerEntityRepository.findById(createOrderRequest.getDryCleanerId())
                .orElseThrow(()-> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        OrderTotalSummaryResponse totalSummary = pricingService.calculateTotalSum(createOrderRequest.getDryCleanerId(),
                createOrderRequest.getOrderItems());
        OrderEntity order = buildOrderEntityUtils(createOrderRequest, customer, dryCleaner, totalSummary);
        buildSummaryResponse(totalSummary, order);
        orderEntityRepository.save(order);
        return buildCreateOrderResponse(order);
    }


    private static @NotNull OrderEntity buildOrderEntityUtils(CreateOrderRequest createOrderRequest,
                                                              CustomerEntity customer, DryCleanerEntity dryCleaner,
                                                              OrderTotalSummaryResponse totalSummary) {
        OrderEntity order = new OrderEntity();
        order.setCustomer(customer);
        order.setDryCleaner(dryCleaner);
        order.setPickupAddress(createOrderRequest.getPickupAddress());
        order.setDeliveryAddress(createOrderRequest.getDeliveryAddress());
        order.setWashingPreference(createOrderRequest.getWashingPreference());
        order.setTotalAmount(totalSummary.getTotal());
        order.setOrderStatus(OrderStatus.CREATED);
        return order;
    }

    private static @NotNull CreateOrderResponse buildCreateOrderResponse(OrderEntity order) {
        CreateOrderResponse createOrderResponse = new CreateOrderResponse();
        createOrderResponse.setCustomerId(order.getCustomer().getId());
        createOrderResponse.setDryCleanerId(order.getDryCleaner().getId());
        createOrderResponse.setDeliveryAddress(order.getDeliveryAddress());
        createOrderResponse.setPickupAddress(order.getPickupAddress());
        createOrderResponse.setCreatedAt(LocalDateTime.now());
        createOrderResponse.setStatus(OrderStatus.PENDING.toString());
        return createOrderResponse;
    }

    private void buildSummaryResponse(OrderTotalSummaryResponse totalSummary, OrderEntity order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemsDTO itemsDTO : totalSummary.getOrderItems()){
            ServiceOffering serviceOffer = serviceOfferingRepository.findById(itemsDTO.getServiceOfferId())
                    .orElseThrow();
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setServiceOffering(serviceOffer);
                orderItem.setQuantity(itemsDTO.getQuantity());
                orderItem.setUnitPrice(itemsDTO.getUnitPrice());
                orderItem.setTotalPrice(itemsDTO.getTotalPrice());
                orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
    }

    private List<String> findMissingService(OrderEntity order, DryCleanerEntity dryCleaner) {
        return null;
    }


}
