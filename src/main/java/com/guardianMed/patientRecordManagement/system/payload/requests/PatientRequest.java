package com.guardianMed.patientRecordManagement.system.payload.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import javax.validation.constraints.NotBlank;

import javax.validation.constraints.*;


@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientRequest {
    @JsonProperty("patientNIC")
    @NotEmpty
    @NotBlank(message = "Patient NIC is required")
    @Size(max = 15, message = "Patient NIC must be at most 15 characters long")
    private String patientNIC;

    @JsonProperty("patientName")
    @NotEmpty
    @NotBlank(message = "Patient name is required")
    @Size(max = 100, message = "Patient name must be at most 100 characters long")
    private String patientName;

    @JsonProperty("gender")
    @NotEmpty
    @NotBlank(message = "Gender is required")
    private String gender;

    @JsonProperty("age")
//    @NotEmpty
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be a positive number")
    private int age;

    @JsonProperty("address")
    @NotEmpty
    @NotNull
    @NotBlank(message = "Address is required")
    private String address;

    @JsonProperty("email")
    @NotEmpty
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @JsonProperty("contactNumber")
//    @NotEmpty
    @NotNull(message = "Contact number is required")
    @Min(value = 0, message = "Contact number must be a positive number")
    private long contactNumber;
}
