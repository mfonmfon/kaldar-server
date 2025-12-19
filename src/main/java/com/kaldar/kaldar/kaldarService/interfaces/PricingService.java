package com.kaldar.kaldar.kaldarService.interfaces;

import com.kaldar.kaldar.domain.entities.OrderItem;
import com.kaldar.kaldar.dtos.response.OrderTotalSummaryResponse;

import java.util.List;

public interface PricingService {
    OrderTotalSummaryResponse calculateTotalSum(Long dryCleanerId, List<OrderItem> serviceItems);

}
