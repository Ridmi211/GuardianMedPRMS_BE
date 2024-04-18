package com.guardianMed.patientRecordManagement.system.repositories;


import com.guardianMed.patientRecordManagement.system.models.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface PatientRepository extends MongoRepository<Patient, String> {
   Patient findByPatientName(String patientName);
   Patient findByPatientNIC(String patientNIC);

}
