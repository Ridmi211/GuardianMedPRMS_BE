package com.guardianMed.patientRecordManagement.system.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@NoArgsConstructor
@Getter@Setter
@Document(collection = "roles")

public class    Role {

    @Id
    private String id;

    private ERole name;

}
