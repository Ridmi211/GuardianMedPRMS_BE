package com.guardianMed.patientRecordManagement.system.controllers;

import com.guardianMed.patientRecordManagement.system.models.Patient;
import com.guardianMed.patientRecordManagement.system.payload.requests.PatientRequest;
import com.guardianMed.patientRecordManagement.system.services.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @PostMapping("/add")
    public ResponseEntity<?> addPatient(@Valid @RequestBody PatientRequest patientRequest) {
        try {
            Patient savedPatient = patientService.savePatient(patientRequest);
            logger.info("Patient created");
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
        } catch (DuplicateKeyException e) {
            logger.error("Failed to add patient: NIC already exists");
            String errorMessage = "Failed to add patient: NIC already exists";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        } catch (Exception e) {
            logger.error("Failed to add patient");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add patient");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Patient>> getPatients() {
        try {
            List<Patient> patients = patientService.getPatients();
            logger.info("Patients retrieved");
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            logger.error("Failed to retrieve patients");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/byID/{id}")
    public ResponseEntity<?> findPatientById(@PathVariable String id) {
        try {
            Patient patient = patientService.getPatientById(id);
            if (patient != null) {
                logger.info("Patient found");
                return ResponseEntity.ok(patient);
            } else {
                logger.error("Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
            }
        } catch (Exception e) {
            logger.error("Unexpected error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/byNIC/{patientNIC}")
    public ResponseEntity<?> findPatientByPatientNIC(@PathVariable String patientNIC) {
        try {
            Patient patient = patientService.getPatientByPatientNIC(patientNIC);
            if (patient != null) {
                logger.info("Patient found");
                return ResponseEntity.ok(patient);
            } else {
                logger.error("Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
            }
        } catch (Exception e) {
            logger.error("Unexpected error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/byName/{patientName}")
    public ResponseEntity<?> findPatientByPatientName(@PathVariable String patientName) {
        try {
            Patient patient = patientService.getPatientByPatientName(patientName);
            if (patient != null) {
                logger.info("Patient found");
                return ResponseEntity.ok(patient);
            } else {
                logger.error("Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
            }
        } catch (Exception e) {
            logger.error("Unexpected error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update/{patientNIC}")
    public ResponseEntity<?> updatePatient(@PathVariable String patientNIC, @RequestBody Patient patient) {
        try {
            Patient updatedPatient = patientService.updatePatient(patientNIC, patient);
            if (updatedPatient != null) {
                logger.info("Patient updated");
                return ResponseEntity.ok(updatedPatient);
            } else {
                logger.error("Patient not found for the given NIC");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found for the given NIC");
            }
        } catch (IllegalArgumentException e) {
            logger.error("NIC cannot be blank");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NIC cannot be blank");
        } catch (DuplicateKeyException e) {
            logger.error("Patient with NIC " + patient.getPatientNIC() + " already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Patient with NIC " + patient.getPatientNIC() + " already exists");
        } catch (Exception e) {
            logger.error("Failed to update patient");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update patient");
        }
    }

    @DeleteMapping("/{patientNIC}")
    public ResponseEntity<?> deletePatient(@PathVariable String patientNIC) {
        try {
            boolean isDeleted = patientService.deletePatient(patientNIC);
            if (isDeleted) {
                logger.info("Patient deleted successfully");
                String successMessage = "Patient with NIC " + patientNIC + " deleted successfully";
                return ResponseEntity.ok(successMessage);
            } else {
                logger.error("Patient not found or already deleted");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found or already deleted");
            }
        } catch (IllegalStateException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to delete patient");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
