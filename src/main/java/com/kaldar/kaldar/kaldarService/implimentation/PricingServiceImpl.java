package com.kaldar.kaldar.kaldarService.implimentation;
import com.kaldar.kaldar.domain.entities.OrderItem;
import com.kaldar.kaldar.domain.entities.ServiceOffering;
import com.kaldar.kaldar.domain.repository.ServiceOfferingRepository;
import com.kaldar.kaldar.dtos.request.OrderItemsDTO;
import com.kaldar.kaldar.dtos.response.OrderTotalSummaryResponse;
import com.kaldar.kaldar.exceptions.ServicesNotFoundException;
import com.kaldar.kaldar.kaldarService.interfaces.PricingService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static com.kaldar.kaldar.contants.StatusResponse.SERVICE_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
public class PricingServiceImpl implements PricingService {

    private final ServiceOfferingRepository serviceOfferingRepository;

    public PricingServiceImpl(ServiceOfferingRepository serviceOfferingRepository) {
        this.serviceOfferingRepository = serviceOfferingRepository;
    }

    @Override
    public OrderTotalSummaryResponse calculateTotalSum(Long dryCleanerId, List<OrderItem> serviceItems) {
        BigDecimal totalSum = BigDecimal.ZERO;
        List<OrderItemsDTO> itemsDTOS = new ArrayList<>();

        for (OrderItem orderItem : serviceItems){
            ServiceOffering serviceOffering = serviceOfferingRepository.findByIdAndDryCleanerId(orderItem.getServiceOffering(), dryCleanerId)
                    .orElseThrow(()-> new ServicesNotFoundException(SERVICE_NOT_FOUND_EXCEPTION_MESSAGE.getMessage()));
            OrderItemsDTO orderItems = buildOrderItemsUtils(orderItem, serviceOffering);
            itemsDTOS.add(orderItems);
        }
        OrderTotalSummaryResponse orderTotalSummaryResponse = new OrderTotalSummaryResponse();
        orderTotalSummaryResponse.setOrderItems(itemsDTOS);
        orderTotalSummaryResponse.setTotal(totalSum);
        return orderTotalSummaryResponse;
    }

    private static @NotNull OrderItemsDTO buildOrderItemsUtils(OrderItem orderItem, ServiceOffering serviceOffering) {
        BigDecimal lineTotal = serviceOffering.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
        OrderItemsDTO orderItems = new OrderItemsDTO();
        orderItems.setServiceOfferId(serviceOffering.getId());
        orderItems.setServiceType(serviceOffering.getServiceType());
        orderItems.setClothType(serviceOffering.getClothType());
        orderItems.setQuantity(orderItem.getQuantity());
        orderItems.setUnitPrice(serviceOffering.getPrice());
        orderItems.setTotalPrice(lineTotal);
        return orderItems;
    }
}
