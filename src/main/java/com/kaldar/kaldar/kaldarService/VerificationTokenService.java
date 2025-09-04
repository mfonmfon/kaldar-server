package com.kaldar.kaldar.kaldarService;

import com.kaldar.kaldar.dtos.request.ResendOtpRequest;
import com.kaldar.kaldar.dtos.request.VerifyOtpRequest;
import com.kaldar.kaldar.dtos.response.VerifyOtpResponse;

public interface VerificationTokenService {

    VerifyOtpResponse verifyOtp(VerifyOtpRequest verifyOtpRequest);

    VerifyOtpResponse resendOtp(ResendOtpRequest resendOtpRequest);
}
