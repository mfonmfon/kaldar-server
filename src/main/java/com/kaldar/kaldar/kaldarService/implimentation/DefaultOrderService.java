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
import com.kaldar.kaldar.dtos.request.OrderItemsDTO;
import com.kaldar.kaldar.dtos.response.CreateOrderResponse;
import com.kaldar.kaldar.dtos.response.OrderLineResponse;
import com.kaldar.kaldar.dtos.response.OrderTotalSummaryResponse;
import com.kaldar.kaldar.exceptions.*;
import com.kaldar.kaldar.kaldarService.interfaces.OrderService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kaldar.kaldar.contants.StatusResponse.*;

@Service
public class DefaultOrderService implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
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
    public AcceptOrderResponse acceptOrder(AcceptOrderRequest acceptOrderRequest) {
        DryCleanerEntity dryCleaner = dryCleanerEntityRepository.findById(acceptOrderRequest.getDryCleanerId())
                .orElseThrow(()-> new UserNotFoundException(DRY_CLEANER_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        OrderEntity order = orderEntityRepository.findById(acceptOrderRequest.getOrderId())
                .orElseThrow(()-> new OrdersNotFoundException(ORDERS_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
        if (order.getDryCleaner() == null || !order.getDryCleaner().getId().equals(dryCleaner.getId())){
            throw new InvalidOrderAssignmentException("Order not assigned to this drycleaner");
        }
        if (order.getOrderStatus() != OrderStatus.PENDING_ACCEPTANCE)
            throw new InvalidOrderAssignmentException("Order cannot be accepted");
        if (Boolean.FALSE.equals(dryCleaner.isActive()))
            throw new NoActiveDryCleanerException(dryCleaner.getId());
        List<String> missingService = findMissingService(order,dryCleaner);
        if (!missingService.isEmpty()){
            throw new MissingServicesNotEmptyException(missingService);
        }
        if (order.getPickupAt() == null)
            order.setPickupAt(LocalDateTime.now().plusHours(24));

        order.setOrderStatus(OrderStatus.SCHEDULED);
        orderEntityRepository.save(order);
        AcceptOrderResponse acceptOrderResponse = new AcceptOrderResponse();
        acceptOrderResponse.setOrderId(order.getId());
        acceptOrderResponse.setStatus("Accepted");
        acceptOrderResponse.setTimestamp(LocalDateTime.now());
        return acceptOrderResponse;
    }

    private List<String> findMissingService(OrderEntity order, DryCleanerEntity dryCleaner) {
        return null;
    }

    @Override
    public CreateOrderResponse placeOrder(CreateOrderRequest createOrderRequest) {
        return null;
    }

    @Override
    public OrderTotalSummaryResponse previewOrder(CreateOrderRequest createOrderRequest) {
        return null;
    }
}
