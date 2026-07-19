package com.kaldar.kaldar.order.service;

import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import com.kaldar.kaldar.order.application.dto.request.SubmitReviewRequest;
import com.kaldar.kaldar.order.application.dto.response.ReviewResponse;
import com.kaldar.kaldar.order.application.service.impl.DefaultReviewService;
import com.kaldar.kaldar.order.domain.model.OrderEntity;
import com.kaldar.kaldar.order.domain.model.ReviewEntity;
import com.kaldar.kaldar.order.domain.repository.OrderEntityRepository;
import com.kaldar.kaldar.order.domain.repository.ReviewRepository;
import com.kaldar.kaldar.shared.domain.constants.OrderStatus;
import com.kaldar.kaldar.shared.domain.exceptions.OrdersNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultReviewService Unit Tests")
class DefaultReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private OrderEntityRepository orderEntityRepository;

    private DefaultReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new DefaultReviewService(reviewRepository, orderEntityRepository);
    }

    // =========================================================================
    // Helper Factories
    // =========================================================================

    private CustomerEntity buildCustomer() {
        CustomerEntity c = new CustomerEntity();
        c.setId(1L);
        c.setFirstName("John");
        c.setLastName("Doe");
        return c;
    }

    private DryCleanerEntity buildDryCleaner() {
        DryCleanerEntity dc = new DryCleanerEntity();
        dc.setId(10L);
        dc.setBusinessName("Sparkle Cleaners");
        return dc;
    }

    private OrderEntity buildCompletedOrder() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setCustomer(buildCustomer());
        order.setDryCleaner(buildDryCleaner());
        return order;
    }

    private ReviewEntity buildReview(OrderEntity order) {
        ReviewEntity review = new ReviewEntity();
        review.setId(1L);
        review.setOrder(order);
        review.setCustomer(order.getCustomer());
        review.setDryCleaner(order.getDryCleaner());
        review.setRating(5);
        review.setComment("Excellent service!");
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }

    // =========================================================================
    // submitReview
    // =========================================================================

    @Nested
    @DisplayName("submitReview()")
    class SubmitReview {

        @Test
        @DisplayName("should submit a review for a COMPLETED order successfully")
        void shouldSubmitReviewForCompletedOrder() {
            OrderEntity order = buildCompletedOrder();
            ReviewEntity savedReview = buildReview(order);

            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));
            when(reviewRepository.findByOrderId(1L)).thenReturn(Optional.empty());
            when(reviewRepository.save(any(ReviewEntity.class))).thenReturn(savedReview);

            SubmitReviewRequest request = new SubmitReviewRequest();
            request.setOrderId(1L);
            request.setRating(5);
            request.setComment("Excellent service!");

            ReviewResponse response = reviewService.submitReview(request);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getRating()).isEqualTo(5);
            assertThat(response.getComment()).isEqualTo("Excellent service!");
            assertThat(response.getCustomerName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("should submit a review for a DELIVERED order")
        void shouldSubmitReviewForDeliveredOrder() {
            OrderEntity order = buildCompletedOrder();
            order.setOrderStatus(OrderStatus.DELIVERED); // DELIVERED is also allowed
            ReviewEntity savedReview = buildReview(order);

            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));
            when(reviewRepository.findByOrderId(1L)).thenReturn(Optional.empty());
            when(reviewRepository.save(any())).thenReturn(savedReview);

            SubmitReviewRequest request = new SubmitReviewRequest();
            request.setOrderId(1L);
            request.setRating(4);
            request.setComment("Good job");

            ReviewResponse response = reviewService.submitReview(request);

            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("should throw IllegalStateException when order is not COMPLETED or DELIVERED")
        void shouldThrowWhenOrderNotCompleted() {
            OrderEntity order = buildCompletedOrder();
            order.setOrderStatus(OrderStatus.CLEANING); // still in progress

            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));

            SubmitReviewRequest request = new SubmitReviewRequest();
            request.setOrderId(1L);
            request.setRating(5);

            assertThatThrownBy(() -> reviewService.submitReview(request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only completed or delivered orders can be reviewed");
        }

        @Test
        @DisplayName("should throw IllegalStateException when order is already reviewed")
        void shouldThrowWhenOrderAlreadyReviewed() {
            OrderEntity order = buildCompletedOrder();
            ReviewEntity existingReview = buildReview(order);

            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));
            when(reviewRepository.findByOrderId(1L)).thenReturn(Optional.of(existingReview));

            SubmitReviewRequest request = new SubmitReviewRequest();
            request.setOrderId(1L);
            request.setRating(3);

            assertThatThrownBy(() -> reviewService.submitReview(request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already reviewed");
        }

        @Test
        @DisplayName("should throw OrdersNotFoundException when order does not exist")
        void shouldThrowWhenOrderNotFound() {
            when(orderEntityRepository.findById(99L)).thenReturn(Optional.empty());

            SubmitReviewRequest request = new SubmitReviewRequest();
            request.setOrderId(99L);
            request.setRating(5);

            assertThatThrownBy(() -> reviewService.submitReview(request))
                    .isInstanceOf(OrdersNotFoundException.class);
        }

        @Test
        @DisplayName("should save review with correct rating value")
        void shouldSaveCorrectRating() {
            OrderEntity order = buildCompletedOrder();
            when(orderEntityRepository.findById(1L)).thenReturn(Optional.of(order));
            when(reviewRepository.findByOrderId(1L)).thenReturn(Optional.empty());
            when(reviewRepository.save(any())).thenAnswer(inv -> {
                ReviewEntity r = inv.getArgument(0);
                r.setId(1L);
                return r;
            });

            SubmitReviewRequest request = new SubmitReviewRequest();
            request.setOrderId(1L);
            request.setRating(4);
            request.setComment("Good service");

            reviewService.submitReview(request);

            ArgumentCaptor<ReviewEntity> captor = ArgumentCaptor.forClass(ReviewEntity.class);
            verify(reviewRepository).save(captor.capture());
            assertThat(captor.getValue().getRating()).isEqualTo(4);
            assertThat(captor.getValue().getComment()).isEqualTo("Good service");
        }
    }

    // =========================================================================
    // getReviewsForDryCleaner
    // =========================================================================

    @Nested
    @DisplayName("getReviewsForDryCleaner()")
    class GetReviewsForDryCleaner {

        @Test
        @DisplayName("should return all reviews for a dry cleaner")
        void shouldReturnReviewsForDryCleaner() {
            OrderEntity order = buildCompletedOrder();
            ReviewEntity r1 = buildReview(order);
            ReviewEntity r2 = buildReview(order);
            r2.setId(2L);
            r2.setRating(3);

            when(reviewRepository.findByDryCleanerIdOrderByCreatedAtDesc(10L))
                    .thenReturn(List.of(r1, r2));

            List<ReviewResponse> reviews = reviewService.getReviewsForDryCleaner(10L);

            assertThat(reviews).hasSize(2);
            assertThat(reviews.get(0).getRating()).isEqualTo(5);
            assertThat(reviews.get(1).getRating()).isEqualTo(3);
        }

        @Test
        @DisplayName("should return empty list when dry cleaner has no reviews")
        void shouldReturnEmptyListForNoReviews() {
            when(reviewRepository.findByDryCleanerIdOrderByCreatedAtDesc(10L)).thenReturn(new ArrayList<>());

            List<ReviewResponse> reviews = reviewService.getReviewsForDryCleaner(10L);

            assertThat(reviews).isEmpty();
        }
    }

    // =========================================================================
    // getReviewByOrderId
    // =========================================================================

    @Nested
    @DisplayName("getReviewByOrderId()")
    class GetReviewByOrderId {

        @Test
        @DisplayName("should return review for a given order ID")
        void shouldReturnReviewForOrder() {
            OrderEntity order = buildCompletedOrder();
            ReviewEntity review = buildReview(order);
            when(reviewRepository.findByOrderId(1L)).thenReturn(Optional.of(review));

            ReviewResponse response = reviewService.getReviewByOrderId(1L);

            assertThat(response).isNotNull();
            assertThat(response.getOrderId()).isEqualTo(1L);
            assertThat(response.getRating()).isEqualTo(5);
        }

        @Test
        @DisplayName("should return null when no review exists for the order")
        void shouldReturnNullWhenNoReview() {
            when(reviewRepository.findByOrderId(1L)).thenReturn(Optional.empty());

            ReviewResponse response = reviewService.getReviewByOrderId(1L);

            assertThat(response).isNull();
        }
    }
}
