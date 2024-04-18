package com.guardianMed.patientRecordManagement.system.controllers;

import com.guardianMed.patientRecordManagement.system.models.Patient;
import com.guardianMed.patientRecordManagement.system.payload.requests.PatientRequest;
import com.guardianMed.patientRecordManagement.system.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RolesAllowed({"", ""})
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/add")
    public ResponseEntity<?> addPatient(@Valid @RequestBody PatientRequest patientRequest) {
        try {
            Patient savedPatient = patientService.savePatient(patientRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
        } catch (DuplicateKeyException e) {
            String errorMessage = "Failed to add patient: NIC already exists";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add patient");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Patient>> getPatients() {
        try {
            List<Patient> patients = patientService.getPatients();
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/byID/{id}")
    public ResponseEntity<?> findPatientById(@PathVariable String id) {
        try {
            Patient patient = patientService.getPatientById(id);
            if (patient != null) {
                return ResponseEntity.ok(patient);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
///    @GetMapping("/byID/{id}")
//    public ResponseEntity<?> findPatientById(@PathVariable String id) {
//        try {
//            Optional<Patient> patient = patientService.getPatientById(id);
//            if (patient != null) {
//                return ResponseEntity.ok(patient);
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
    @GetMapping("/byNIC/{patientNIC}")
    public ResponseEntity<?> findPatientByPatientNIC(@PathVariable String patientNIC) {
        try {
            Patient patient = patientService.getPatientByPatientNIC(patientNIC);
            if (patient != null) {
                return ResponseEntity.ok(patient);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/byName/{patientName}")
    public ResponseEntity<?> findPatientByPatientName(@PathVariable String patientName) {
        try {
            Patient patient = patientService.getPatientByPatientName(patientName);
            if (patient != null) {
                return ResponseEntity.ok(patient);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update/{patientNIC}")
    public ResponseEntity<?> updatePatient(@PathVariable String patientNIC, @RequestBody Patient patient) {
        try {
            Patient updatedPatient = patientService.updatePatient(patientNIC, patient);
            if (updatedPatient != null) {
                return ResponseEntity.ok(updatedPatient);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found for the given NIC");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NIC cannot be blank");
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Patient with NIC " + patient.getPatientNIC() + " already exists");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update patient");
        }
    }


    @DeleteMapping("/{patientNIC}")
    public ResponseEntity<?> deletePatient(@PathVariable String patientNIC) {
        try {
            boolean isDeleted = patientService.deletePatient(patientNIC);
            if (isDeleted) {
                String successMessage = "Patient with NIC " + patientNIC + " deleted successfully";
                return ResponseEntity.ok(successMessage);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found or already deleted");
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
