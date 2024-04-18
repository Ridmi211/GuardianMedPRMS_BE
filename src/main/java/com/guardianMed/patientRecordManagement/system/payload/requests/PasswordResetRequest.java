package com.guardianMed.patientRecordManagement.system.payload.requests;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Component
@Data
public class PasswordResetRequest {

    @NotEmpty(message = "Previous password must not be empty")
    private String previousPassword;

    @NotEmpty(message = "New password must not be empty")
    @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;

    @NotEmpty(message = "Confirm password must not be empty")
    private String confirmPassword;
}
