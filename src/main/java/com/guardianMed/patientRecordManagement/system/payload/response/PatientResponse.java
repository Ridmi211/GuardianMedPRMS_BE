package com.guardianMed.patientRecordManagement.system.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponse {

    private String id;

    @NotBlank
    private String patientNIC;

    private String patientName;

    private String gender;

    private int age;

    private String address;

    private String email;

    private long contactNumber;


}
