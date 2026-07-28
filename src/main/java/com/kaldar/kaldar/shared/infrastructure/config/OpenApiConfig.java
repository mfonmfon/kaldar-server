package com.kaldar.kaldar.shared.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;

import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        Schema<?> errorSchema = new Schema<>()
                .type("object")
                .addProperty("message", new Schema<String>().type("string").example("User-friendly error message for frontend display"))
                .addProperty("error", new Schema<String>().type("string").example("Bad Request"))
                .addProperty("status", new Schema<Integer>().type("integer").example(400))
                .addProperty("path", new Schema<String>().type("string").example("/api/v1/resource"))
                .addProperty("details", new Schema<Object>().type("object").nullable(true))
                .addProperty("timestamp", new Schema<String>().type("string").example("2026-07-28T01:45:00"));

        return new OpenAPI()
                .info(new Info()
                        .title("Kaldar API Specification")
                        .description("Production REST API documentation for Kaldar Monorepo — Laundry & Dry Cleaning Platform. " +
                                "All endpoints document 200 responses as well as user-friendly error responses (400, 401, 403, 404, 409, 422, 500).")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Kaldar Engineering Team")
                                .email("engineering@kaldar.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://localhost:8050").description("Local Development Server"),
                        new Server().url("https://kaldar-server.onrender.com").description("Production / Staging Server (Render)")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT bearer token acquired from `/api/v1/auth/login`"))
                        .addSchemas("ApiErrorResponse", errorSchema));
    }

    @Bean
    public OpenApiCustomizer globalErrorResponsesCustomizer() {
        return openApi -> {
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                ApiResponses responses = operation.getResponses();

                if (!responses.containsKey("400")) {
                    responses.addApiResponse("400", createApiResponse("400 Bad Request: Invalid input or missing required fields. Please check your request details and try again."));
                }
                if (!responses.containsKey("401")) {
                    responses.addApiResponse("401", createApiResponse("401 Unauthorized: Your session has expired or credentials are invalid. Please log in to continue."));
                }
                if (!responses.containsKey("403")) {
                    responses.addApiResponse("403", createApiResponse("403 Forbidden: Access denied. You do not have permission to perform this action."));
                }
                if (!responses.containsKey("404")) {
                    responses.addApiResponse("404", createApiResponse("404 Not Found: The requested resource, record, or user could not be found."));
                }
                if (!responses.containsKey("409")) {
                    responses.addApiResponse("409", createApiResponse("409 Conflict: Record already exists (e.g. registered email or phone number)."));
                }
                if (!responses.containsKey("422")) {
                    responses.addApiResponse("422", createApiResponse("422 Unprocessable Entity: Business rule violation (e.g. insufficient wallet balance)."));
                }
                if (!responses.containsKey("500")) {
                    responses.addApiResponse("500", createApiResponse("500 Internal Server Error: An unexpected error occurred on the server. Please try again later."));
                }
            }));
        };
    }

    private ApiResponse createApiResponse(String description) {
        Content content = new Content().addMediaType("application/json",
                new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiErrorResponse")));
        return new ApiResponse().description(description).content(content);
    }
}
