package com.kaldar.kaldar;

import com.kaldar.kaldar.dtos.request.CreateOrderRequest;
import com.kaldar.kaldar.dtos.response.CreateOrderResponse;
import com.kaldar.kaldar.kaldarService.interfaces.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void testThat_CanCreatOrder(){
        Long customerId = 1L;
        Long dryCleanerId = 1L;
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId(customerId);
        createOrderRequest.setDryCleanerId(dryCleanerId);
        createOrderRequest.setPickupAddress("Lekki phase 1, Lagos, Nigeria");
        createOrderRequest.setPickupAddress("Ikate, Lagos, Nigeria");
        createOrderRequest.setOrderItemsRequests(List.of());
        CreateOrderResponse createOrderResponse = orderService.createOrder(createOrderRequest);
        assertThat(createOrderResponse).isNotNull();
    }
}
