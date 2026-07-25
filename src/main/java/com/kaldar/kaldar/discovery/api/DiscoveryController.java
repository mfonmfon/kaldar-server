package com.kaldar.kaldar.discovery.api;

import com.kaldar.kaldar.discovery.application.dto.request.FindAvailableDrycleanersRequest;
import com.kaldar.kaldar.shared.api.response.ApiResponse;
import com.kaldar.kaldar.discovery.application.dto.response.AvailableDryCleanerResponse;
import com.kaldar.kaldar.discovery.application.service.DryCleanerQueryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DiscoveryController {

        private final DryCleanerQueryService dryCleanerQueryService;

        public DiscoveryController(DryCleanerQueryService dryCleanerQueryService) {
                this.dryCleanerQueryService = dryCleanerQueryService;
        }

        @GetMapping("/drycleaners")
        public ResponseEntity<ApiResponse<Page<AvailableDryCleanerResponse>>> findAvailableDryCleaners(
                        @Valid @ModelAttribute FindAvailableDrycleanersRequest request) {
                Page<AvailableDryCleanerResponse> result = dryCleanerQueryService.findAvailable(request);
                ApiResponse<Page<AvailableDryCleanerResponse>> api = ApiResponse
                                .<Page<AvailableDryCleanerResponse>>builder()
                                .isSuccess(true)
                                .status(HttpStatus.OK.value())
                                .message("Drycleaners fetched")
                                .data(result)
                                .build();
                return ResponseEntity.ok(api);
        }
}
