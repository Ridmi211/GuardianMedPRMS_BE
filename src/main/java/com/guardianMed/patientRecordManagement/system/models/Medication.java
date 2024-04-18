package com.guardianMed.patientRecordManagement.system.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Medication {

    @NotBlank
    private String name;

    @NotNull
    private Double price;

    @NotNull
    @Min(0)
    private Integer quantity;
}
