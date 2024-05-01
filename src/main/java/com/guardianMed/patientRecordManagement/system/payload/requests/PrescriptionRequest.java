package com.guardianMed.patientRecordManagement.system.payload.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Component
@Data
@Getter
@Setter
public class PrescriptionRequest {
    @NotBlank(message = "Patient NIC is required")
    private String patientNIC;

    @NotBlank(message = "Doctor ID is required")
    private String doctorID;

    @NotBlank(message = "Doctor name is required")
    private String doctorName;

    private String patientName;

    private LocalDate date;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    @NotNull(message = "Medications are required")
    @Size(min = 1, message = "At least one medication must be specified")
    private List<String> medications;

    @NotBlank(message = "Instructions are required")
    private String instructions;


}