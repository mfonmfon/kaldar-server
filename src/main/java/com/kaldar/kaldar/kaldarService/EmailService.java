package com.kaldar.kaldar.kaldarService;

import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;
import org.springframework.stereotype.Service;


public interface EmailService {

    SendVerificationEmailResponse sendVerificationEmail(String email, String otpDigitsNumbersGenerate);


}
