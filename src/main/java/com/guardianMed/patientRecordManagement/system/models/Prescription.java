package com.guardianMed.patientRecordManagement.system.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "prescriptions")
@Getter
@Setter

public class Prescription {

    @Id
    private String id;

    private String patientNIC;

    private String doctorID;

    private String doctorName;

    private String patientName;

    private LocalDate date;

    private String diagnosis;

    private List<String> medications;

    private String instructions;

    private State state;

    private Patient patient;

    public enum State{
        ACTIVE,
        INACTIVE,
    }

}
