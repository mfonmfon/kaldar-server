package com.kaldar.kaldar.discovery.application.service;

import com.kaldar.kaldar.discovery.application.dto.request.FindAvailableDrycleanersRequest;
import com.kaldar.kaldar.discovery.application.dto.response.AvailableDryCleanerResponse;
import org.springframework.data.domain.Page;

public interface DryCleanerQueryService {
    Page<AvailableDryCleanerResponse> findAvailable(FindAvailableDrycleanersRequest request);

}
