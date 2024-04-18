package com.guardianMed.patientRecordManagement.system.payload.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class SignupRequest {
    @NotBlank(message = "Field is required")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "Field is required")
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> roles;

    @NotBlank(message = "Field is required")
    @Size(min = 6, max = 40)
    private String password;
}
