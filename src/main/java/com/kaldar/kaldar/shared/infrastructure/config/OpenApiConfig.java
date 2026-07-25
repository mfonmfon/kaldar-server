package com.kaldar.kaldar.shared.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Kaldar API Specification")
                        .description("Production REST API documentation for Kaldar Monorepo — Laundry & Dry Cleaning Platform. " +
                                "Covers Notification, Favourite, Wallet, Payment (Paystack), Order, Customer, and Dry Cleaner services.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Kaldar Engineering Team")
                                .email("engineering@kaldar.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server"),
                        new Server().url("https://api.kaldar.com").description("Staging / Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT bearer token acquired from `/api/v1/auth/login`")));
    }
}
