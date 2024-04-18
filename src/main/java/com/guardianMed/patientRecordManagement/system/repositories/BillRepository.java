package com.guardianMed.patientRecordManagement.system.repositories;

import com.guardianMed.patientRecordManagement.system.models.Bill;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
public interface BillRepository extends MongoRepository<Bill, String> {
    Bill findBillByPatientNIC(String patientNIC);
    Bill findBillByPatientName(String patientName);
    List<Bill> findAllByPatientNIC(String patientNIC);

}
