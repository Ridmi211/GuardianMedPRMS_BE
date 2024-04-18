package com.guardianMed.patientRecordManagement.system.services;

import com.guardianMed.patientRecordManagement.system.models.Bill;
import com.guardianMed.patientRecordManagement.system.models.Prescription;
import com.guardianMed.patientRecordManagement.system.payload.requests.BillRequest;
import com.guardianMed.patientRecordManagement.system.repositories.BillRepository;
import com.guardianMed.patientRecordManagement.system.repositories.PrescriptionRepository;
import com.guardianMed.patientRecordManagement.system.models.Medication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BillService {

    private static final Logger log = (Logger) LoggerFactory.getLogger(Bill.class);
    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PatientService patientService;


    //create a bill
    public Bill createBillFromRequest(Authentication authentication, BillRequest billRequest, String prescriptionId) {
        Bill bill = null;

        String userId = authentication.getName(); // Get the user ID from the authenticated user

        // Retrieve patient NIC and patient name from the prescription
        Prescription existingPrescription = prescriptionService.getPrescriptionById(prescriptionId);
        if (existingPrescription == null) {
            log.error("Prescription not found");
        } else if (existingPrescription.getState().equals(Prescription.State.INACTIVE)) {
            log.error("Prescription is already inactive");
        } else {
            String patientNIC = existingPrescription.getPatientNIC();
            String patientName = existingPrescription.getPatientName();
            double totalAmount = calculateTotalAmount(billRequest.getMedications());

            bill = new Bill();
            bill.setAdminId(userId); // Set the admin ID to the logged-in user ID
            bill.setCustomerBillId(generateBillId());
            bill.setPatientNIC(patientNIC);
            bill.setPatientName(patientName);
            bill.setDate(LocalDate.now());
            bill.setMedications(billRequest.getMedications());
            bill.setTotalAmount(totalAmount);
            bill.setDoctorsMedication(existingPrescription.getMedications());

            log.info("Bill created with ID: " + bill.getCustomerBillId());

            existingPrescription.setState(Prescription.State.INACTIVE);
            prescriptionRepository.save(existingPrescription);

            bill = billRepository.save(bill); // Save the bill and retrieve its ID

            log.info("Bill saved with ID: " + bill.getId());
            // Retrieve the prescription and bill details
            String prescriptionDetails = getPrescriptionDetails(existingPrescription);
            String billDetails = getBillDetails(bill);

            // Construct the message with prescription and bill details
            try {
                // Construct the message with prescription and bill details
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("Dear Patient,\n");
                messageBuilder.append("We hope this email finds you well. We are writing to confirm the successful payment for your recent medical consultation and prescribed \n");
                messageBuilder.append("medications. Thank you for promptly settling the bill. Please find below the summary of prescription details and payment confirmation:\n\n");
                messageBuilder.append(prescriptionDetails).append("\n");
                messageBuilder.append("Bill Details:\n");
                messageBuilder.append(billDetails).append("\n");
                messageBuilder.append("Should you have any further queries or require additional information, please do not hesitate to contact our dedicated support team \n");
                messageBuilder.append("at +9481 456 7890 or via email at medicare@gmail.com. We are here to assist you with any concerns you may have.\n\n");
                messageBuilder.append("Thank you once again for choosing our services. We value your trust in our healthcare provider and remain committed \n");
                messageBuilder.append("to delivering high-quality care to our patients.\n\n");
                messageBuilder.append("Wishing you continued good health and well-being.\n\n");
                messageBuilder.append("Warm regards,\n\n");
                messageBuilder.append("Healthcare Provider/MediCareLanka\n");



                String patientEmail = patientService.getEmailByPatientNIC(patientNIC);// Replace with the patient's email address
                String subject = "Your Bill and Prescription Details";
                String message = messageBuilder.toString();
                emailService.sendEmail(patientEmail, subject, message);


                log.info("Email sent to patient: " + patientEmail);
            } catch (Exception e) {

                log.error("Failed to send email to patient: " + e.getMessage());

            }

        }

        return bill;
    }

    public String generateBillId() {
        int min = 100;
        int max = 999;

        Random random = new Random();
        String formattedBillNumber = String.format("%02d", random.nextInt(max - min + 1) + min);
        return "BILL-" + formattedBillNumber;
    }

    private String getPrescriptionDetails(Prescription prescription) {
        StringBuilder prescriptionDetailsBuilder = new StringBuilder();

//        prescriptionDetailsBuilder.append("Patient NIC: ").append(prescription.getPatientNIC()).append("\n");
        if (prescription.getPatient() != null) {
            prescriptionDetailsBuilder.append("Patient Details: ").append("\n");
            prescriptionDetailsBuilder.append("  Patient Name: ").append(prescription.getPatient().getPatientName()).append("\n");
            prescriptionDetailsBuilder.append("  Patient NIC: ").append(prescription.getPatient().getPatientNIC()).append("\n");
            prescriptionDetailsBuilder.append("  Gender: ").append(prescription.getPatient().getGender()).append("\n");
            prescriptionDetailsBuilder.append("  Age: ").append(prescription.getPatient().getAge()).append("\n");
            prescriptionDetailsBuilder.append("  Address: ").append(prescription.getPatient().getAddress()).append("\n");
            prescriptionDetailsBuilder.append("  Email: ").append(prescription.getPatient().getEmail()).append("\n");
            prescriptionDetailsBuilder.append("  Contact Number: ").append(prescription.getPatient().getContactNumber()).append("\n\n");

        }
//        prescriptionDetailsBuilder.append("Prescription Details: ").append("\n");
        prescriptionDetailsBuilder.append("Prescription ID: ").append(prescription.getId()).append("\n");
        prescriptionDetailsBuilder.append("Date: ").append(prescription.getDate()).append("\n");
        prescriptionDetailsBuilder.append("Doctor ID: ").append(prescription.getDoctorID()).append("\n");
        prescriptionDetailsBuilder.append("Doctor Name: ").append(prescription.getDoctorName()).append("\n");
        prescriptionDetailsBuilder.append("Diagnosis: ").append(prescription.getDiagnosis()).append("\n");
        prescriptionDetailsBuilder.append("Medications: ").append(prescription.getMedications()).append("\n");
        prescriptionDetailsBuilder.append("Instructions: ").append(prescription.getInstructions()).append("\n");// Include other prescription details as needed
        return prescriptionDetailsBuilder.toString();
    }

    private String getBillDetails(Bill bill) {
        StringBuilder billDetailsBuilder = new StringBuilder();
//        billDetailsBuilder.append("Bill Details: ").append("\n");
        billDetailsBuilder.append("Bill ID: ").append(bill.getCustomerBillId()).append("\n");
        billDetailsBuilder.append("Admin ID: ").append(bill.getAdminId()).append("\n");
        billDetailsBuilder.append("Patient NIC: ").append(bill.getPatientNIC()).append("\n");
        billDetailsBuilder.append("Patient Name: ").append(bill.getPatientName()).append("\n");
        billDetailsBuilder.append("Date: ").append(bill.getDate()).append("\n");
        billDetailsBuilder.append("Prescribed medications").append(bill.getDoctorsMedication()).append("\n");
        billDetailsBuilder.append("Issued medications: ").append(bill.getMedications()).append("\n");
        billDetailsBuilder.append("Total Amount (Rs): ").append(bill.getTotalAmount()).append("\n");

        return billDetailsBuilder.toString();
    }



    // Get all bills
    public List<Bill> getBills() {
        log.info("Retrieving all bills");
        return billRepository.findAll();
    }

    // Get a specific bill using bill id
    public Bill getBillById(String id) {
        log.info("Retrieving bill by ID: " + id);
        return billRepository.findById(id).orElse(null);
    }

    // Get all bills by patient NIC
    public List<Bill> getBillsByPatientNIC(String patientNIC) {

        log.info("Retrieving bills by patient NIC: " + patientNIC);
        return billRepository.findAllByPatientNIC(patientNIC);
    }

    // Delete bill by bill id
    public ResponseEntity<?> deleteBill(String id) {
        Optional<Bill> optionalBill = billRepository.findById(id);
        if (optionalBill.isPresent()) {
            Bill bill = optionalBill.get();
            billRepository.delete(bill);
            return ResponseEntity.ok("Bill with ID " + id + " deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bill with ID " + id + " not found");
    }



    private Bill updateBill(Bill bill) {
        return bill;
    }

    // Calculate total amount for a list of medications
    public double calculateTotalAmount(List<Medication> medications) {
        double totalAmount = 0.0;
        for (Medication medication : medications) {
            double price = medication.getPrice();
            int quantity = medication.getQuantity();
            double medicationTotal = price * quantity;
            totalAmount += medicationTotal;
        }
        log.info("Total amount calculated: " + totalAmount);
        return totalAmount;
    }

}

