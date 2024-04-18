package com.guardianMed.patientRecordManagement.system.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bills")
@Getter
@Setter

public class Bill {

    @Id
    private String id;

    private String customerBillId;

    private String adminId;

    private String patientNIC;

    private String patientName;

    private LocalDate date;

    private List<Medication> medications;
    
    private List<String> doctorsMedication;

    private double totalAmount;

}
