package com.kaldar.kaldar.order.application.service;

import com.kaldar.kaldar.order.application.dto.request.AcceptOrderRequest;
import com.kaldar.kaldar.order.application.dto.response.AcceptOrderResponse;
import com.kaldar.kaldar.order.application.dto.request.CreateOrderRequest;
import com.kaldar.kaldar.order.application.dto.request.UpdateOrderStatusRequest;
import com.kaldar.kaldar.order.application.dto.response.CreateOrderResponse;
import com.kaldar.kaldar.order.application.dto.response.UpdateOrderStatusResponse;

public interface OrderService {
    AcceptOrderResponse acceptOrder(AcceptOrderRequest acceptOrderRequest);

    CreateOrderResponse placeOrder(CreateOrderRequest createOrderRequest);

    UpdateOrderStatusResponse updateOrderStatus(UpdateOrderStatusRequest request);
}
