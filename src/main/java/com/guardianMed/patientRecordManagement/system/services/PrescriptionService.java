package com.guardianMed.patientRecordManagement.system.services;

import com.guardianMed.patientRecordManagement.system.models.Patient;
import com.guardianMed.patientRecordManagement.system.models.Prescription;
import com.guardianMed.patientRecordManagement.system.repositories.PatientRepository;
import com.guardianMed.patientRecordManagement.system.repositories.PrescriptionRepository;
import com.guardianMed.patientRecordManagement.system.payload.requests.PrescriptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PrescriptionService {
    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PatientRepository patientRepository;

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

    /**
     * Save a prescription.
     */
    public Prescription savePrescription(@Valid PrescriptionRequest prescriptionRequest) {
        String patientNIC = prescriptionRequest.getPatientNIC();
        Patient patient = patientRepository.findByPatientNIC(patientNIC);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found for the given patientNIC: " + patientNIC + ". Please register the patient first.");
        }

        Prescription prescription = new Prescription();
        prescription.setPatientNIC(patientNIC);
        prescription.setPatient(patient);
        prescription.setDoctorID(prescriptionRequest.getDoctorID());
        prescription.setDoctorName(prescriptionRequest.getDoctorName());
        prescription.setPatientName(patient.getPatientName());
        prescription.setDate(LocalDate.now());
        prescription.setDiagnosis(prescriptionRequest.getDiagnosis());
        prescription.setMedications(prescriptionRequest.getMedications());
        prescription.setInstructions(prescriptionRequest.getInstructions());
        prescription.setState(Prescription.State.ACTIVE);

        logger.info("Saving prescription for patient: {}", patient.getPatientName());
        return prescriptionRepository.save(prescription);
    }

    /**
     * Get all prescriptions.
     */
    public List<Prescription> getPrescriptions() {
        logger.info("Retrieving all prescriptions");
        return prescriptionRepository.findAll();
    }

    /**
     * Get a specific prescription by ID.
     */
    public Prescription getPrescriptionById(String id) {
        Optional<Prescription> prescriptionOptional = prescriptionRepository.findById(id);
        if (prescriptionOptional.isPresent()) {
            Prescription prescription = prescriptionOptional.get();
            logger.info("Retrieved prescription with ID: {}", id);
            return prescription;
        } else {
            throw new NoSuchElementException("Prescription not found for the given ID: " + id);
        }
    }

    /**
     * Get a specific prescription by patient name.
     */
    public Prescription getPrescriptionByPatientName(String patientName) {
        Prescription prescription = prescriptionRepository.findByPatientName(patientName);
        if (prescription != null) {
            logger.info("Retrieved prescription for patient: {}", patientName);
            return prescription;
        } else {
            throw new NoSuchElementException("Prescription not found for the given patient name: " + patientName);
        }
    }

    /**
     * Get all prescriptions by patient NIC.
     */
    public List<Prescription> getPrescriptionsByPatientNIC(String patientNIC) {
        List<Prescription> prescriptions = prescriptionRepository.findByPatientNIC(patientNIC);
        if (!prescriptions.isEmpty()) {
            logger.info("Retrieved prescriptions for patient with NIC: {}", patientNIC);
            return prescriptions;
        } else {
            throw new NoSuchElementException("Prescriptions not found for the given patient NIC: " + patientNIC);
        }
    }

    /**
     * Update a prescription.
     */
    public Prescription updatePrescription(String id, Prescription prescription) {
        Optional<Prescription> existingPrescriptionOptional = prescriptionRepository.findById(id);
        if (existingPrescriptionOptional.isPresent()) {
            Prescription existingPrescription = existingPrescriptionOptional.get();
            if (existingPrescription.getState() == Prescription.State.INACTIVE) {
                throw new IllegalStateException("Cannot update an inactive prescription.");
            }
            existingPrescription.setDate(LocalDate.now());
            existingPrescription.setDiagnosis(prescription.getDiagnosis());
            existingPrescription.setMedications(prescription.getMedications());
            existingPrescription.setInstructions(prescription.getInstructions());

            logger.info("Updating prescription with ID: {}", id);
            return prescriptionRepository.save(existingPrescription);
        } else {
            throw new NoSuchElementException("Prescription not found for the given ID: " + id);
        }
    }

    /**
     * Delete a prescription.
     */
    public boolean deletePrescription(String id) {
        Optional<Prescription> existingPrescriptionOptional = prescriptionRepository.findById(id);
        if (existingPrescriptionOptional.isPresent()) {
            Prescription existingPrescription = existingPrescriptionOptional.get();
            LocalDate creationDate = existingPrescription.getDate();
            LocalDate currentDate = LocalDate.now();
            long yearsBetween = ChronoUnit.YEARS.between(creationDate, currentDate);

            if (yearsBetween < 5) {
                throw new IllegalStateException("Prescription can only be deleted after 5 years from the date of creation.");
            }
            logger.info("Deleting prescription with ID: {}", id);
            prescriptionRepository.deleteById(id);
            return true;
        } else {
            throw new NoSuchElementException("Prescription not found for the given ID: " + id);
        }
    }
}

