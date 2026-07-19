package com.kaldar.kaldar.order.application.service;

import com.kaldar.kaldar.order.application.dto.request.SubmitReviewRequest;
import com.kaldar.kaldar.order.application.dto.response.ReviewResponse;
import java.util.List;

public interface ReviewService {
    ReviewResponse submitReview(SubmitReviewRequest request);
    List<ReviewResponse> getReviewsForDryCleaner(Long dryCleanerId);
    ReviewResponse getReviewByOrderId(Long orderId);
}
