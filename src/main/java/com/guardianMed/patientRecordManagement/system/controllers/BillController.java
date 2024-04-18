package com.guardianMed.patientRecordManagement.system.controllers;

import com.guardianMed.patientRecordManagement.system.models.Bill;
import com.guardianMed.patientRecordManagement.system.models.Prescription;
import com.guardianMed.patientRecordManagement.system.payload.requests.BillRequest;
import com.guardianMed.patientRecordManagement.system.repositories.PrescriptionRepository;
import com.guardianMed.patientRecordManagement.system.services.BillService;
import com.guardianMed.patientRecordManagement.system.services.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bills")
@CrossOrigin(origins = "http://localhost:4200")
@RolesAllowed({"", ""})
public class BillController {
    private static final Logger logger = LoggerFactory.getLogger(BillController.class);

    @Autowired
    private BillService billService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    /**
     * Endpoint to add a bill.
     */

    @PostMapping("/add/{prescriptionId}")
    public ResponseEntity<?> addBill(@PathVariable String prescriptionId, @RequestBody @Valid BillRequest billRequest, Authentication authentication) {

        try {
            logger.info("Adding bill: {}", billRequest);

            Prescription existingPrescription = prescriptionService.getPrescriptionById(prescriptionId);
            if (existingPrescription == null) {
                logger.error("Prescription not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Prescription not found");
            } else if (existingPrescription.getState().equals(Prescription.State.INACTIVE)) {
                logger.error("Prescription is already inactive");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prescription is already inactive");
            }

            Bill savedBill = billService.createBillFromRequest(authentication, billRequest, prescriptionId);

            if (savedBill != null) {
                logger.info("Bill added successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(savedBill);
            } else {
                logger.error("Failed to add bill");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add bill");
            }
        } catch (Exception e) {
            logger.error("Error occurred while adding bill: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while adding bill");
        }
    }

    /**
     * Endpoint to get all bills.
     */

    @GetMapping("/all")
    public ResponseEntity<List<Bill>> getBills() {
        try {
            logger.info("Retrieving all bills");
            List<Bill> bills = billService.getBills();
            return ResponseEntity.ok(bills);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving bills: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint to find a bill by ID.
     */

    @GetMapping("/byID/{id}")
    public ResponseEntity<?> findBillById(@PathVariable String id) {
        try {
            logger.info("Retrieving bill with ID: {}", id);
            Bill bill = billService.getBillById(id);
            if (bill != null) {
                logger.info("Bill found");
                return ResponseEntity.ok(bill);
            } else {
                logger.error("Bill not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bill not found");
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving bill: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint to find bills by patient NIC.
     */
    @GetMapping("/byNIC/{patientNIC}")
    public ResponseEntity<?> findBillsByPatientNIC(@PathVariable String patientNIC) {
        try {
            logger.info("Retrieving bills with patient NIC: {}", patientNIC);
            List<Bill> bills = billService.getBillsByPatientNIC(patientNIC);
            if (!bills.isEmpty()) {
                logger.info("Bills found");
                return ResponseEntity.ok(bills);
            } else {
                logger.error("Bills not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bills not found");
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving bills: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    /**
     * Endpoint to delete a bill.
     */

    // Endpoint to delete a bill.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBill(@PathVariable String id) {
        try {
            logger.info("Deleting bill with ID: {}", id);
            ResponseEntity<?> response = billService.deleteBill(id);
            if (response.getBody().equals("Deletion cancelled")) {
                logger.info("Deletion cancelled by user");
                return ResponseEntity.ok("Deletion cancelled by user");
            } else if (response.getBody().equals("Bill with ID " + id + " deleted successfully")) {
                logger.info("Bill deleted successfully");
                return ResponseEntity.ok("Bill with ID " + id + " deleted successfully");
            } else {
                logger.error("Failed to delete bill");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bill with ID " + id + " not found or already deleted");
            }
        } catch (Exception e) {
            logger.error("Error occurred while deleting bill: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
