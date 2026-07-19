package com.kaldar.kaldar.dispatch.service;

import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.dispatch.application.dto.LogisticsDeliveryRequest;
import com.kaldar.kaldar.dispatch.application.dto.LogisticsDeliveryResponse;
import com.kaldar.kaldar.dispatch.application.service.LogisticsProvider;
import com.kaldar.kaldar.dispatch.application.service.impl.DefaultLogisticsService;
import com.kaldar.kaldar.dispatch.domain.model.DeliveryTask;
import com.kaldar.kaldar.dispatch.domain.repository.DeliveryTaskRepository;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import com.kaldar.kaldar.order.domain.model.OrderEntity;
import com.kaldar.kaldar.order.domain.repository.OrderEntityRepository;
import com.kaldar.kaldar.shared.domain.constants.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultLogisticsService Unit Tests")
class DefaultLogisticsServiceTest {

    @Mock private DeliveryTaskRepository deliveryTaskRepository;
    @Mock private OrderEntityRepository orderEntityRepository;
    @Mock private LogisticsProvider mockProvider;
    @Mock private com.kaldar.kaldar.wallet.application.service.WalletService walletService;

    private DefaultLogisticsService logisticsService;

    @BeforeEach
    void setUp() {
        lenient().when(mockProvider.getProviderName()).thenReturn("MOCK");
        logisticsService = new DefaultLogisticsService(
                deliveryTaskRepository, orderEntityRepository, List.of(mockProvider), walletService
        );
        ReflectionTestUtils.setField(logisticsService, "activeProviderName", "MOCK");
    }

    private OrderEntity buildOrder() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.ACCEPTED);
        
        CustomerEntity customer = new CustomerEntity();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setPhoneNumber("+2348011111111");
        order.setCustomer(customer);

        DryCleanerEntity dryCleaner = new DryCleanerEntity();
        dryCleaner.setBusinessName("Dry Cleaner Express");
        dryCleaner.setBusinessAddress("5 DryClean Street");
        dryCleaner.setPhoneNumber("+2348022222222");
        order.setDryCleaner(dryCleaner);

        order.setPickupAddress("10 Customer Lane");
        return order;
    }

    @Test
    @DisplayName("should request pickup delivery successfully and store DeliveryTask")
    void shouldRequestPickupDelivery() {
        OrderEntity order = buildOrder();
        LogisticsDeliveryResponse apiResponse = new LogisticsDeliveryResponse("ext-123", "REQUESTED", "http://track.me", "1500.0");
        
        when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));
        when(mockProvider.requestDelivery(any(LogisticsDeliveryRequest.class))).thenReturn(apiResponse);
        when(deliveryTaskRepository.save(any(DeliveryTask.class))).thenAnswer(inv -> inv.getArgument(0));

        DeliveryTask result = logisticsService.requestPickupDelivery(1L);

        assertThat(result).isNotNull();
        assertThat(result.getExternalDeliveryId()).isEqualTo("ext-123");
        assertThat(result.getStatus()).isEqualTo("REQUESTED");
        assertThat(result.getProvider()).isEqualTo("MOCK");

        ArgumentCaptor<LogisticsDeliveryRequest> captor = ArgumentCaptor.forClass(LogisticsDeliveryRequest.class);
        verify(mockProvider).requestDelivery(captor.capture());
        
        LogisticsDeliveryRequest mappedRequest = captor.getValue();
        assertThat(mappedRequest.getPickupName()).isEqualTo("John Doe");
        assertThat(mappedRequest.getDropoffAddress()).isEqualTo("5 DryClean Street");
    }

    @Test
    @DisplayName("should update task and order status when receiving Webhook for PICKED_UP")
    void shouldUpdateStatusOnWebhook() {
        OrderEntity order = buildOrder();
        DeliveryTask task = new DeliveryTask();
        task.setOrder(order);
        task.setType("PICKUP");
        task.setStatus("REQUESTED");

        when(deliveryTaskRepository.findByProviderAndExternalDeliveryId("CHOWDECK", "ext-123"))
                .thenReturn(Optional.of(task));

        Map<String, Object> payload = new HashMap<>();
        payload.put("category", "PICKED_UP");
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", "ext-123");
        payload.put("payload", data);

        logisticsService.handleWebhookCallback("CHOWDECK", payload);

        assertThat(task.getStatus()).isEqualTo("PICKED_UP");
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PICKED);
        
        verify(deliveryTaskRepository).save(task);
        verify(orderEntityRepository).save(order);
    }
}
