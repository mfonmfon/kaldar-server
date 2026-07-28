package com.kaldar.kaldar.shared.infrastructure.config;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MailConfiguration {

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String host;

    @Value("${spring.mail.port:465}")
    private int primaryPort;

    @Value("${spring.mail.fallback-port:587}")
    private int fallbackPort;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Bean
    @Primary
    @Qualifier("primaryMailer")
    public Mailer primaryMailer() {
        return buildMailerForPort(primaryPort);
    }

    @Bean
    @Qualifier("fallbackMailer")
    public Mailer fallbackMailer() {
        return buildMailerForPort(fallbackPort);
    }

    private Mailer buildMailerForPort(int port) {
        TransportStrategy strategy = (port == 465)
                ? TransportStrategy.SMTPS
                : TransportStrategy.SMTP_TLS;

        return MailerBuilder
                .withSMTPServer(host, port, username, password)
                .withTransportStrategy(strategy)
                .withSessionTimeout(15_000)
                .withDebugLogging(false)
                .buildMailer();
    }
}
