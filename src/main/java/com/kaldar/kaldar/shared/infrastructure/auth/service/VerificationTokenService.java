package com.kaldar.kaldar.shared.infrastructure.auth.service;

import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.ResendOtpRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.VerifyOtpRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.VerifyOtpResponse;

public interface VerificationTokenService {
    VerifyOtpResponse verifyOtp(VerifyOtpRequest verifyOtpRequest);
    VerifyOtpResponse resendOtp(ResendOtpRequest resendOtpRequest);
}
