package com.guardianMed.patientRecordManagement.system.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "patients")
@Getter
@Setter

public class Patient {

    @Id
    private String id;

    @NotBlank
    @Indexed(unique = true)
    private String patientNIC;

    private String patientName;

    private String gender;

    private int age;

    private String address;

    private String email;

    private long contactNumber;


    public Patient(String nic123, String johnDoe, String male, int i, String s, String mail, int i1) {
    }
}
