package com.kaldar.kaldar.order.application.service.impl;

import com.kaldar.kaldar.order.application.dto.request.SubmitReviewRequest;
import com.kaldar.kaldar.order.application.dto.response.ReviewResponse;
import com.kaldar.kaldar.order.application.service.ReviewService;
import com.kaldar.kaldar.order.domain.model.OrderEntity;
import com.kaldar.kaldar.order.domain.model.ReviewEntity;
import com.kaldar.kaldar.order.domain.repository.OrderEntityRepository;
import com.kaldar.kaldar.order.domain.repository.ReviewRepository;
import com.kaldar.kaldar.shared.domain.constants.OrderStatus;
import com.kaldar.kaldar.shared.domain.exceptions.OrdersNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultReviewService implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderEntityRepository orderEntityRepository;

    public DefaultReviewService(ReviewRepository reviewRepository, OrderEntityRepository orderEntityRepository) {
        this.reviewRepository = reviewRepository;
        this.orderEntityRepository = orderEntityRepository;
    }

    @Override
    @Transactional
    public ReviewResponse submitReview(SubmitReviewRequest request) {
        OrderEntity order = orderEntityRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrdersNotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.COMPLETED && order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Only completed or delivered orders can be reviewed");
        }

        if (reviewRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new IllegalStateException("Order already reviewed");
        }

        ReviewEntity review = new ReviewEntity();
        review.setOrder(order);
        review.setCustomer(order.getCustomer());
        review.setDryCleaner(order.getDryCleaner());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        ReviewEntity savedReview = reviewRepository.save(review);
        return mapToReviewResponse(savedReview);
    }

    @Override
    public List<ReviewResponse> getReviewsForDryCleaner(Long dryCleanerId) {
        return reviewRepository.findByDryCleanerIdOrderByCreatedAtDesc(dryCleanerId)
                .stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse getReviewByOrderId(Long orderId) {
        return reviewRepository.findByOrderId(orderId)
                .map(this::mapToReviewResponse)
                .orElse(null);
    }

    private ReviewResponse mapToReviewResponse(ReviewEntity review) {
        ReviewResponse resp = new ReviewResponse();
        resp.setId(review.getId());
        resp.setOrderId(review.getOrder().getId());
        resp.setRating(review.getRating());
        resp.setComment(review.getComment());
        resp.setCreatedAt(review.getCreatedAt());
        if (review.getCustomer() != null) {
            resp.setCustomerName(review.getCustomer().getFirstName() + " " + review.getCustomer().getLastName());
        }
        return resp;
    }
}
