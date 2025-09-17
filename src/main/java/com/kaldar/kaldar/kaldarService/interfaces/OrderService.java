package com.kaldar.kaldar.kaldarService.interfaces;

import com.kaldar.kaldar.dtos.request.AcceptOrderRequest;
import com.kaldar.kaldar.dtos.request.AcceptOrderResponse;
import com.kaldar.kaldar.dtos.request.CreateOrderRequest;
import com.kaldar.kaldar.dtos.response.CreateOrderResponse;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest);
    AcceptOrderResponse acceptOrder(AcceptOrderRequest acceptOrderRequest);

}
