package com.guardianMed.patientRecordManagement.system.payload.response;

import com.guardianMed.patientRecordManagement.system.models.Medication;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Data

public class BillResponse {

    private String id;

    private String adminId;

    private String patientNIC;

    private String patientName;

    private LocalDate date;

    private List<Medication> medications;

    private double totalAmount;

}
