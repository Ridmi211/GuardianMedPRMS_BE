package com.guardianMed.patientRecordManagement.system.services;

import com.guardianMed.patientRecordManagement.system.exception.NotFoundException;
import com.guardianMed.patientRecordManagement.system.models.Patient;
import com.guardianMed.patientRecordManagement.system.models.Prescription;
import com.guardianMed.patientRecordManagement.system.repositories.PatientRepository;
import com.guardianMed.patientRecordManagement.system.repositories.PrescriptionRepository;
import com.guardianMed.patientRecordManagement.system.payload.requests.PatientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Service
public class PatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    public Patient savePatient(@Valid PatientRequest patientRequest) {
        logger.info("Trying to save patient: {}", patientRequest);

        Patient patient = new Patient();
        patient.setPatientNIC(patientRequest.getPatientNIC());
        patient.setPatientName(patientRequest.getPatientName());
        patient.setGender(patientRequest.getGender());
        patient.setAge(patientRequest.getAge());
        patient.setAddress(patientRequest.getAddress());
        patient.setEmail(patientRequest.getEmail());
        patient.setContactNumber(patientRequest.getContactNumber());

        if (patientRepository.findByPatientNIC(patient.getPatientNIC()) != null) {
            logger.info("Couldn't save.Patient with NIC " + patient.getPatientNIC() + " already exists");
            throw new DuplicateKeyException("Patient with NIC " + patient.getPatientNIC() + " already exists");
                  }

        Patient savedPatient = patientRepository.save(patient);
        logger.info("Patient saved successfully: {}", savedPatient);

        return savedPatient;
    }

    public List<Patient> getPatients() {
        logger.info("Fetching all patients");
        return patientRepository.findAll();
    }

    public Patient getPatientById(String id) {
        logger.info("Fetching patient by ID: {}", id);
        return patientRepository.findById(id).orElse(null);
    }

    public Patient getPatientByPatientName(String patientName) {
        logger.info("Fetching patient by name: {}", patientName);
        return patientRepository.findByPatientName(patientName);
    }

    public Patient getPatientByPatientNIC(String patientNIC) {
        logger.info("Fetching patient by NIC: {}", patientNIC);
        return patientRepository.findByPatientNIC(patientNIC);
    }

    public Patient updatePatient(String patientNIC, Patient patient) {
        logger.info("Updating patient with NIC: {}", patientNIC);

        Patient existingPatient = patientRepository.findByPatientNIC(patientNIC);
        if (existingPatient != null) {
            String newPatientNIC = patient.getPatientNIC();

            // Check if the NIC is blank or null
            if (newPatientNIC == null || newPatientNIC.trim().isEmpty()) {
                throw new IllegalArgumentException("NIC cannot be blank");
            }

            // Check if the new NIC is different from the existing NIC
            if (!newPatientNIC.equals(patientNIC)) {
                // Check if the new NIC already exists
                Patient duplicatePatient = patientRepository.findByPatientNIC(newPatientNIC);
                if (duplicatePatient != null) {
                    throw new DuplicateKeyException("Patient with NIC " + newPatientNIC + " already exists");
                }
            }

            existingPatient.setPatientName(patient.getPatientName());
            existingPatient.setGender(patient.getGender());
            existingPatient.setAge(patient.getAge());
            existingPatient.setAddress(patient.getAddress());
            existingPatient.setEmail(patient.getEmail());
            existingPatient.setContactNumber(patient.getContactNumber());

            Patient updatedPatient = patientRepository.save(existingPatient);
            logger.info("Patient updated successfully: {}", updatedPatient);

            return updatedPatient;
        } else {
            return null;
        }
    }

    public boolean deletePatient(String patientNIC) {
        logger.info("Deleting patient with NIC: {}", patientNIC);

        Patient patient = patientRepository.findByPatientNIC(patientNIC);
        if (patient != null) {
            // Check if there are any prescriptions associated with the patient's NIC
            List<Prescription> prescriptions = prescriptionRepository.findByPatientNIC(patientNIC);
            if (!prescriptions.isEmpty()) {
                throw new IllegalStateException("Cannot delete patient with existing prescriptions");
            }

            patientRepository.delete(patient);
            logger.info("Patient deleted successfully");
            return true;
        }

        logger.info("Patient not found with NIC: {}", patientNIC);
        return false;
    }

    public String getEmailByPatientNIC(String patientNIC) {
        logger.info("Fetching email for patient with NIC: {}", patientNIC);

        Patient patient = patientRepository.findByPatientNIC(patientNIC);
        if (patient != null) {
            String email = patient.getEmail();
            logger.info("Email fetched successfully: {}", email);
            return email;
        }

        logger.info("Patient not found with NIC: {}", patientNIC);
        throw new NotFoundException("Patient with NIC " + patientNIC + " not found");
    }
}
