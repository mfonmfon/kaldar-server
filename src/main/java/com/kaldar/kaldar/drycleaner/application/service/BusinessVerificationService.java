package com.kaldar.kaldar.drycleaner.application.service;

import com.kaldar.kaldar.drycleaner.application.dto.request.VerifyBusinessRequest;
import com.kaldar.kaldar.drycleaner.application.dto.response.VerifyBusinessResponse;

public interface BusinessVerificationService {

    VerifyBusinessResponse submitBusinessVerification(VerifyBusinessRequest request, String cacDocumentUrl);

    VerifyBusinessResponse getVerificationStatus(Long dryCleanerId);
}
