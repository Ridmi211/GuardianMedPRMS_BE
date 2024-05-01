package com.guardianMed.patientRecordManagement.system.payload.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record LoginRequest(
        @JsonProperty("username")

        @NotBlank(message = "Field is required")
        String username,
        @NotBlank(message = "Field is required")
        String password

) {
}
