package com.kaldar.kaldar.kaldarService.implimentation;

import com.kaldar.kaldar.dtos.request.SendVerificationEmailRequest;
import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.exceptions.EmailSendException;
import com.kaldar.kaldar.kaldarService.interfaces.EmailService;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DefaultEmailService implements EmailService {

    @Value("${spring.mail.from-name}")
    private String fromName;

    @Value("${spring.mail.from-email}")
    private String fromEmail;

    private final Mailer mailer;


    @Value("${security.otp.expiry-minutes}")
    private int otpExpiryMinutes;

    public DefaultEmailService(Mailer mailer) {
        this.mailer = mailer;
    }

    @Override
    public SendVerificationEmailResponse sendVerificationEmail(String recipientEmail, String otpDigitNumberGenerator) {
        try {
            Email email = EmailBuilder.startingBlank()
                    .from(fromName, fromEmail)
                    .to(recipientEmail)
                    .withSubject(" Your Verification Code ")
                    .withPlainText("Your verification code is: " + otpDigitNumberGenerator +
                            "\n\nIt expires in " + otpExpiryMinutes + " minutes.")
                    .withHTMLText(
                            "<h2 style='color:#2c3e50;'>Verification Code</h2>" +
                                    "<p>Your OTP is: <b style='font-size:18px;'>" +
                                    otpDigitNumberGenerator+ "</b></p>" + "<p>This code will expire in <b>" +
                                    otpExpiryMinutes + "</b> minutes.</p>")
                    .buildEmail();
            mailer.sendMail(email);
            return new SendVerificationEmailResponse(recipientEmail, otpDigitNumberGenerator,
                    "Verification code sent successfully"
            );
        } catch (Exception ex) {
            throw new EmailSendException(ex.getMessage());
        }
    }
}