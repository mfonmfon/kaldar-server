package com.kaldar.kaldar.order.api;

import com.kaldar.kaldar.order.application.dto.request.SubmitReviewRequest;
import com.kaldar.kaldar.order.application.dto.response.ReviewResponse;
import com.kaldar.kaldar.order.application.service.ReviewService;
import com.kaldar.kaldar.shared.api.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ReviewResponse>> submitReview(@Valid @RequestBody SubmitReviewRequest request) {
        ReviewResponse response = reviewService.submitReview(request);
        ApiResponse<ReviewResponse> api = ApiResponse.<ReviewResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.CREATED.value())
                .message("Review submitted successfully")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(api);
    }

    @GetMapping("/drycleaner/{dryCleanerId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getDryCleanerReviews(@PathVariable Long dryCleanerId) {
        List<ReviewResponse> response = reviewService.getReviewsForDryCleaner(dryCleanerId);
        ApiResponse<List<ReviewResponse>> api = ApiResponse.<List<ReviewResponse>>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Reviews retrieved")
                .data(response)
                .build();
        return ResponseEntity.ok(api);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewByOrderId(@PathVariable Long orderId) {
        ReviewResponse response = reviewService.getReviewByOrderId(orderId);
        ApiResponse<ReviewResponse> api = ApiResponse.<ReviewResponse>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Review retrieved")
                .data(response)
                .build();
        return ResponseEntity.ok(api);
    }
}
