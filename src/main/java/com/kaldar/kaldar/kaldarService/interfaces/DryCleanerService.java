package com.kaldar.kaldar.kaldarService.interfaces;

import com.kaldar.kaldar.dtos.request.AcceptOrderRequest;
import com.kaldar.kaldar.dtos.request.AcceptOrderResponse;
import com.kaldar.kaldar.dtos.request.DryCleanerRegistrationRequest;
import com.kaldar.kaldar.dtos.request.UpdateDryCleanerProfileRequest;
import com.kaldar.kaldar.dtos.response.DryCleanerProfileResponse;
import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;

public interface DryCleanerService {

    SendVerificationEmailResponse registerDryCleaner(DryCleanerRegistrationRequest dryCleanerRegistrationRequest);

    DryCleanerProfileResponse editProfile(UpdateDryCleanerProfileRequest updateDryCleanerProfileRequest);

}
