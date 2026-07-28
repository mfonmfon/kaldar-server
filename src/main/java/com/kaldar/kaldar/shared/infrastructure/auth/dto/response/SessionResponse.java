package com.kaldar.kaldar.shared.infrastructure.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {

    @Schema(description = "User Database ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "User Email Address", example = "user@kaldar.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "User First Name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @Schema(description = "User Last Name", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Schema(description = "User Assigned Roles", example = "[\"CUSTOMER\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<String> roles;

    @Schema(description = "User Type classification", example = "CUSTOMER", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userType;

    @Schema(description = "Customer ID if user is a Customer", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long customerId;

    @Schema(description = "Dry Cleaner ID if user is a Dry Cleaner", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long dryCleanerId;
}
