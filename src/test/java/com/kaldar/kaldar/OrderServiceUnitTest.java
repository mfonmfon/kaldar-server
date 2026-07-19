package com.kaldar.kaldar;

import com.kaldar.kaldar.order.application.dto.request.AcceptOrderRequest;
import com.kaldar.kaldar.order.application.dto.request.CreateOrderRequest;
import com.kaldar.kaldar.order.application.dto.response.AcceptOrderResponse;
import com.kaldar.kaldar.order.application.dto.response.CreateOrderResponse;
import com.kaldar.kaldar.order.application.service.impl.DefaultOrderService;
import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.customer.domain.repository.CustomerEntityRepository;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import com.kaldar.kaldar.drycleaner.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.drycleaner.domain.repository.ServiceOfferingRepository;
import com.kaldar.kaldar.order.domain.model.OrderEntity;
import com.kaldar.kaldar.order.domain.repository.OrderEntityRepository;
import com.kaldar.kaldar.order.domain.repository.OrderServiceItemRepository;
import com.kaldar.kaldar.shared.domain.constants.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {

    private DefaultOrderService orderService;

    @Mock private CustomerEntityRepository customerEntityRepository;
    @Mock private DryCleanerEntityRepository dryCleanerEntityRepository;
    @Mock private ServiceOfferingRepository serviceOfferingRepository;
    @Mock private OrderEntityRepository orderEntityRepository;
    @Mock private OrderServiceItemRepository orderServiceItemRepository;
    @Mock private ApplicationEventPublisher applicationEventPublisher;
    @Mock private com.kaldar.kaldar.order.domain.repository.ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        orderService = new DefaultOrderService(
                customerEntityRepository, dryCleanerEntityRepository,
                serviceOfferingRepository, orderEntityRepository,
                orderServiceItemRepository, applicationEventPublisher,
                reviewRepository
        );
    }

    @Test
    void testPlaceOrder() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(1L);
        request.setServiceItems(Collections.emptyList());

        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);

        when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderEntityRepository.save(any())).thenAnswer(i -> {
            OrderEntity o = i.getArgument(0);
            o.setId(100L);
            return o;
        });

        CreateOrderResponse response = orderService.placeOrder(request);

        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(100L);
    }

    @Test
    void testAcceptOrder() {
        AcceptOrderRequest request = new AcceptOrderRequest();
        request.setOrderId(1L);
        request.setDryCleanerId(1L);

        DryCleanerEntity dryCleaner = new DryCleanerEntity();
        dryCleaner.setId(1L);
        dryCleaner.setActive(true);

        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setDryCleaner(dryCleaner);
        order.setOrderStatus(OrderStatus.PENDING_ACCEPTANCE);

        when(dryCleanerEntityRepository.findById(1L)).thenReturn(Optional.of(dryCleaner));
        when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderEntityRepository.save(any())).thenReturn(order);

        AcceptOrderResponse response = orderService.acceptOrder(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("ACCEPTED");
    }

    @Test
    void testSubmitReview() {
        com.kaldar.kaldar.order.application.dto.request.SubmitReviewRequest request = new com.kaldar.kaldar.order.application.dto.request.SubmitReviewRequest();
        request.setOrderId(1L);
        request.setCustomerId(1L);
        request.setDryCleanerId(1L);
        request.setRating(5);
        when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(new OrderEntity()));
        when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(new CustomerEntity()));
        when(dryCleanerEntityRepository.findById(1L)).thenReturn(Optional.of(new DryCleanerEntity()));
        when(reviewRepository.save(any())).thenAnswer(i -> {
            com.kaldar.kaldar.order.domain.model.ReviewEntity r = i.getArgument(0);
            r.setId(500L);
            return r;
        });

        com.kaldar.kaldar.order.application.dto.response.SubmitReviewResponse response = orderService.submitReview(request);

        assertThat(response).isNotNull();
        assertThat(response.getReviewId()).isEqualTo(500L);
    }

    @Test
    void testRejectOrder() {
        com.kaldar.kaldar.order.application.dto.request.RejectOrderRequest request = new com.kaldar.kaldar.order.application.dto.request.RejectOrderRequest();
        request.setOrderId(1L);
        request.setDryCleanerId(1L);
        request.setReason("Out of capacity");

        DryCleanerEntity dryCleaner = new DryCleanerEntity();
        dryCleaner.setId(1L);

        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setDryCleaner(dryCleaner);
        order.setOrderStatus(OrderStatus.PENDING_ACCEPTANCE);

        when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));
        when(dryCleanerEntityRepository.findById(1L)).thenReturn(Optional.of(dryCleaner));
        when(orderEntityRepository.save(any())).thenReturn(order);

        com.kaldar.kaldar.order.application.dto.response.RejectOrderResponse response = orderService.rejectOrder(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("REJECTED");
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECTED);
        assertThat(order.getRejectionReason()).isEqualTo("Out of capacity");
    }
}
