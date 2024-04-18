package com.guardianMed.patientRecordManagement.system.payload.requests;

import com.guardianMed.patientRecordManagement.system.models.Medication;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

@Component
@Data
public class BillRequest {

    @Id
    private String id;

    private String adminId;

    private String patientNIC;

    private String patientName;

    private LocalDate date;

    @NotEmpty(message = "Medications are required")
    private List<Medication> medications;

    private double totalAmount;

}
