package com.guardianMed.patientRecordManagement.system;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardianMed.patientRecordManagement.system.controllers.PatientController;
import com.guardianMed.patientRecordManagement.system.models.Patient;
import com.guardianMed.patientRecordManagement.system.payload.requests.PatientRequest;
import com.guardianMed.patientRecordManagement.system.services.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }
    @Test
    void testAddValidPatient() throws Exception {
        // Arrange
        PatientRequest request = new PatientRequest("NIC123", "John Doe", "Male", 30, "123 Street, City", "john@example.com", 1234567890L);
        Patient savedPatient = new Patient("NIC123", "John Doe", "Male", 30, "123 Street, City", "john@example.com", 1234567890);
        when(patientService.savePatient(any())).thenReturn(savedPatient);

        // Act
        ResponseEntity<?> responseEntity = patientController.addPatient(request);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Patient responseBody = objectMapper.readValue(objectMapper.writeValueAsString(responseEntity.getBody()), Patient.class);
        assertEquals(savedPatient, responseBody);
    }

    @Test
    void testAddPatientWithExistingNIC() {
        // Arrange
        PatientRequest request = new PatientRequest("NIC123", "John Doe", "Male", 30, "123 Street, City", "john@example.com", 1234567890L);
        when(patientService.savePatient(any())).thenThrow(new DuplicateKeyException("Duplicate NIC"));

        // Act
        ResponseEntity<?> responseEntity = patientController.addPatient(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Failed to add patient: NIC already exists", responseEntity.getBody());
    }
    @Test
    void testGetPatients() {
        // Arrange
        List<Patient> patients = new ArrayList<>();
        patients.add(new Patient("1", "NIC123", "John Doe", "Male", 30, "123 Street, City", "john@example.com", 1234567890L));
        patients.add(new Patient("2", "NIC456", "Jane Doe", "Female", 25, "456 Street, City", "jane@example.com", 9876543210L));
        when(patientService.getPatients()).thenReturn(patients);

        // Act
        ResponseEntity<List<Patient>> responseEntity = patientController.getPatients();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(patients, responseEntity.getBody());
    }

    @Test
    void testAddPatient_Success() {
        // Arrange
        PatientRequest request = new PatientRequest("NIC789", "Alice Smith", "Female", 35, "789 Avenue, Town", "alice@example.com", 9876543210L);
        Patient patient = new Patient("3", "NIC789", "Alice Smith", "Female", 35, "789 Avenue, Town", "alice@example.com", 9876543210L);
        when(patientService.savePatient(any(PatientRequest.class))).thenReturn(patient);

        // Act
        ResponseEntity<?> responseEntity = patientController.addPatient(request);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(patient, responseEntity.getBody());
    }

    @Test
    void testAddPatient_DuplicateNIC() {
        // Arrange
        PatientRequest request = new PatientRequest("NIC123", "John Doe", "Male", 30, "123 Street, City", "john@example.com", 1234567890L);
        when(patientService.savePatient(any(PatientRequest.class))).thenThrow(new DuplicateKeyException("NIC already exists"));

        // Act
        ResponseEntity<?> responseEntity = patientController.addPatient(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Failed to add patient: NIC already exists", responseEntity.getBody());
    }


    @Test
    void testFindPatientById_ExistingId() {
        // Arrange
        String patientId = "1";
        Patient patient = new Patient(patientId, "NIC123", "John Doe", "Male", 30, "123 Street, City", "john@example.com", 1234567890L);
        when(patientService.getPatientById(patientId)).thenReturn(patient);

        // Act
        ResponseEntity<?> responseEntity = patientController.findPatientById(patientId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(patient, responseEntity.getBody());
    }

    @Test
    void testFindPatientById_NonExistingId() {
        // Arrange
        String patientId = "1";
        when(patientService.getPatientById(patientId)).thenReturn(null);

        // Act
        ResponseEntity<?> responseEntity = patientController.findPatientById(patientId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Patient not found", responseEntity.getBody());
    }

    @Test
    void testFindPatientByPatientNIC_ExistingNIC() {
        // Arrange
        String patientNIC = "NIC123";
        Patient patient = new Patient("1", patientNIC, "John Doe", "Male", 30, "123 Street, City", "john@example.com", 1234567890L);
        when(patientService.getPatientByPatientNIC(patientNIC)).thenReturn(patient);

        // Act
        ResponseEntity<?> responseEntity = patientController.findPatientByPatientNIC(patientNIC);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(patient, responseEntity.getBody());
    }

    @Test
    void testFindPatientByPatientNIC_NonExistingNIC() {
        // Arrange
        String patientNIC = "NIC123";
        when(patientService.getPatientByPatientNIC(patientNIC)).thenReturn(null);

        // Act
        ResponseEntity<?> responseEntity = patientController.findPatientByPatientNIC(patientNIC);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Patient not found", responseEntity.getBody());
    }

    @Test
    void testFindPatientByPatientName_ExistingName() {
        // Arrange
        String patientName = "John Doe";
        Patient patient = new Patient("1", "NIC123", patientName, "Male", 30, "123 Street, City", "john@example.com", 1234567890L);
        when(patientService.getPatientByPatientName(patientName)).thenReturn(patient);

        // Act
        ResponseEntity<?> responseEntity = patientController.findPatientByPatientName(patientName);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(patient, responseEntity.getBody());
    }

    @Test
    void testFindPatientByPatientName_NonExistingName() {
        // Arrange
        String patientName = "John Doe";
        when(patientService.getPatientByPatientName(patientName)).thenReturn(null);

        // Act
        ResponseEntity<?> responseEntity = patientController.findPatientByPatientName(patientName);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Patient not found", responseEntity.getBody());
    }

    @Test
    void testUpdatePatient_Success() {
        // Arrange
        String patientNIC = "NIC123";
        Patient patientToUpdate = new Patient("1", patientNIC, "John Doe", "Male", 30, "123 Street, City", "john@example.com", 1234567890L);
        Patient updatedPatient = new Patient("1", patientNIC, "John Doe", "Male", 35, "123 Street, City", "john@example.com", 1234567890L);
        when(patientService.updatePatient(eq(patientNIC), any(Patient.class))).thenReturn(updatedPatient);

        // Act
        ResponseEntity<?> responseEntity = patientController.updatePatient(patientNIC, updatedPatient);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedPatient, responseEntity.getBody());
    }

    @Test
    void testUpdatePatient_NonExistingNIC() {
        // Arrange
        String patientNIC = "NIC123";
        Patient updatedPatient = new Patient("1", patientNIC, "John Doe", "Male", 35, "123 Street, City", "john@example.com", 1234567890L);
        when(patientService.updatePatient(eq(patientNIC), any(Patient.class))).thenReturn(null);

        // Act
        ResponseEntity<?> responseEntity = patientController.updatePatient(patientNIC, updatedPatient);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Patient not found for the given NIC", responseEntity.getBody());
    }

    // Add other test cases for remaining endpoints
}
