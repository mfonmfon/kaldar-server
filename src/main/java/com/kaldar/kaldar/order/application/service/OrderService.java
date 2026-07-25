package com.kaldar.kaldar.order.application.service;

import com.kaldar.kaldar.order.application.dto.request.AcceptOrderRequest;
import com.kaldar.kaldar.order.application.dto.response.AcceptOrderResponse;
import com.kaldar.kaldar.order.application.dto.request.CreateOrderRequest;
import com.kaldar.kaldar.order.application.dto.request.UpdateOrderStatusRequest;
import com.kaldar.kaldar.order.application.dto.response.CreateOrderResponse;
import com.kaldar.kaldar.order.application.dto.response.UpdateOrderStatusResponse;
import com.kaldar.kaldar.order.application.dto.response.OrderDetailsResponse;

public interface OrderService {
    AcceptOrderResponse acceptOrder(AcceptOrderRequest acceptOrderRequest);

    CreateOrderResponse placeOrder(CreateOrderRequest createOrderRequest);

    UpdateOrderStatusResponse updateOrderStatus(UpdateOrderStatusRequest request);

    OrderDetailsResponse getOrderById(Long orderId);

    java.util.List<OrderDetailsResponse> getOrdersByCustomerId(Long customerId);

    java.util.List<OrderDetailsResponse> getOrdersByDryCleanerId(Long dryCleanerId);

    com.kaldar.kaldar.order.application.dto.response.SubmitReviewResponse submitReview(
            com.kaldar.kaldar.order.application.dto.request.SubmitReviewRequest request);

    com.kaldar.kaldar.order.application.dto.response.RejectOrderResponse rejectOrder(
            com.kaldar.kaldar.order.application.dto.request.RejectOrderRequest request);
}
