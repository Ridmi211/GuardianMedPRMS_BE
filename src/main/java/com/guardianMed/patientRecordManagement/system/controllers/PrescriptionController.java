package com.guardianMed.patientRecordManagement.system.controllers;

import com.guardianMed.patientRecordManagement.system.models.Prescription;
import com.guardianMed.patientRecordManagement.system.payload.requests.PrescriptionRequest;
import com.guardianMed.patientRecordManagement.system.services.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/prescription")
@CrossOrigin(origins = "http://localhost:4200")
@RolesAllowed({"", ""})


public class PrescriptionController {
    private static final Logger logger = LoggerFactory.getLogger(PrescriptionController.class);

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping("/add")
    public ResponseEntity<Object> savePrescription(@Valid @RequestBody PrescriptionRequest prescriptionRequest) {
        Prescription savedPrescription = prescriptionService.savePrescription(prescriptionRequest);
        if (savedPrescription != null) {
            logger.info("Prescription saved successfully with ID: {}", savedPrescription.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPrescription);
        } else {
            logger.error("Failed to save prescription");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An error occurred while saving the prescription");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Prescription>> getPrescriptions() {
        List<Prescription> prescriptions = prescriptionService.getPrescriptions();
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/byID/{id}")
    public ResponseEntity<?> findPrescriptionById(@PathVariable String id) {
        Prescription prescription = prescriptionService.getPrescriptionById(id);
        if (prescription != null) {
            logger.info("Prescription found");
            return ResponseEntity.ok(prescription);
        } else {
            logger.error("Prescription not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Prescription not found");
        }
    }

    @GetMapping("/byName/{patientName}")
    public ResponseEntity<?> findPrescriptionByPatientName(@PathVariable String patientName) {
        Prescription prescription = prescriptionService.getPrescriptionByPatientName(patientName);
        if (prescription != null) {
            logger.info("Prescription found");
            return ResponseEntity.ok(prescription);
        } else {
            logger.error("Prescription not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Prescription not found");
        }
    }

    @GetMapping("/byNIC/{patientNIC}")
    public ResponseEntity<?> findPrescriptionsByPatientNIC(@PathVariable String patientNIC) {
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatientNIC(patientNIC);
        if (!prescriptions.isEmpty()) {
            logger.info("Prescriptions found");
            return ResponseEntity.ok(prescriptions);
        } else {
            logger.error("Prescriptions not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Prescriptions not found");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePrescription(@PathVariable String id, @RequestBody Prescription prescription) {
        Prescription updatedPrescription = prescriptionService.updatePrescription(id, prescription);
        if (updatedPrescription != null) {
            logger.info("Prescription successfully updated");
            return ResponseEntity.ok(updatedPrescription);
        } else {
            logger.error("Failed to update prescription");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update prescription");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrescription(@PathVariable String id) {
        try {
            prescriptionService.deletePrescription(id);
            logger.info("Prescription deleted successfully");
            String successMessage = "Prescription with ID " + id + " deleted successfully";
            return ResponseEntity.ok(successMessage);
        } catch (NoSuchElementException e) {
            logger.error("Failed to delete prescription");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Prescription with ID " + id + " not found or already deleted");
        } catch (IllegalStateException e) {
            logger.error("Failed to delete prescription");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
