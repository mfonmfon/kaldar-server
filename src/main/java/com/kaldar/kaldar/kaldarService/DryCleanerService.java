package com.kaldar.kaldar.kaldarService;

import com.kaldar.kaldar.dtos.request.DryCleanerRegistrationRequest;
import com.kaldar.kaldar.dtos.response.DryCleanerRegistrationResponse;
import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;

public interface DryCleanerService {

    SendVerificationEmailResponse registerDryCleaner(DryCleanerRegistrationRequest dryCleanerRegistrationRequest);

}
