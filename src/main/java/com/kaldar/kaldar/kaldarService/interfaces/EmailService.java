package com.kaldar.kaldar.kaldarService.interfaces;

import com.kaldar.kaldar.dtos.request.SendVerificationEmailRequest;
import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;


public interface EmailService {

    SendVerificationEmailResponse sendVerificationEmail(String email, String otpDigitNumberGenerator);
}
