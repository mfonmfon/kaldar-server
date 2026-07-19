package com.kaldar.kaldar.drycleaner.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateWorkingHoursRequest {
    @NotNull(message = "Dry cleaner ID is required")
    private Long dryCleanerId;

    @NotBlank(message = "Working hours JSON is required")
    private String workingHoursJson;

    // Getters and Setters
    public Long getDryCleanerId() { return dryCleanerId; }
    public void setDryCleanerId(Long dryCleanerId) { this.dryCleanerId = dryCleanerId; }

    public String getWorkingHoursJson() { return workingHoursJson; }
    public void setWorkingHoursJson(String workingHoursJson) { this.workingHoursJson = workingHoursJson; }
}
