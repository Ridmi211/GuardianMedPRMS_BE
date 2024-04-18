package com.guardianMed.patientRecordManagement.system.payload.response;

import com.guardianMed.patientRecordManagement.system.models.Prescription;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Data
public class PrescriptionResponse {

    private String id;

    private String patientNIC;

    private String doctorID;

    private String doctorName;

    private String patientName;

    private LocalDate date;

    private String diagnosis;

    private List<String> medications;

    private String instructions;

    private Prescription.State state;
}
