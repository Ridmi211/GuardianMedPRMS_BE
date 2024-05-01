package com.guardianMed.patientRecordManagement.system;

import com.guardianMed.patientRecordManagement.system.controllers.AdminController;
import com.guardianMed.patientRecordManagement.system.exception.NotFoundException;
import com.guardianMed.patientRecordManagement.system.models.User;
import com.guardianMed.patientRecordManagement.system.payload.requests.PasswordResetRequest;
import com.guardianMed.patientRecordManagement.system.services.AdminService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void testGetAdminById_Success() {
        String id = "1";
        User admin = new User();
        admin.setId(id);
        when(adminService.getAdminById(id)).thenReturn(admin);
        ResponseEntity<?> responseEntity = adminController.getAdminById(id);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(admin, responseEntity.getBody());
    }

    @Test
    void testGetAdminById_NotFound() {
        String id = "999";
        when(adminService.getAdminById(id)).thenThrow(new NotFoundException("Admin not found for ID: " + id));
        ResponseEntity<?> responseEntity = adminController.getAdminById(id);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void testGetAdminByName_Success() {
        String name = "adminName";
        User admin = new User();
        admin.setUsername(name);
        when(adminService.getAdminByName(name)).thenReturn(admin);
        ResponseEntity<?> responseEntity = adminController.getAdminByName(name);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(admin, responseEntity.getBody());
    }

    @Test
    void testGetAdminByName_NotFound() {
        String name = "nonExistingAdminName";
        when(adminService.getAdminByName(name)).thenThrow(new NotFoundException("Admin not found for name: " + name));
        ResponseEntity<?> responseEntity = adminController.getAdminByName(name);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void testUpdateAdmin_Success() {
        String id = "1";
        User admin = new User();
        admin.setId(id);
        when(adminService.updateAdmin(eq(id), any())).thenReturn(admin);
        ResponseEntity<?> responseEntity = adminController.updateAdmin(id, admin);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(admin, responseEntity.getBody());
    }

    @Test
    void testUpdateAdmin_NotFound() {
        String id = "999";
        when(adminService.updateAdmin(eq(id), any())).thenThrow(new NotFoundException("Admin not found for ID: " + id));
        ResponseEntity<?> responseEntity = adminController.updateAdmin(id, new User());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void testUpdateAdmin_BadRequest() {
        String id = "1";
        when(adminService.updateAdmin(eq(id), any())).thenThrow(new IllegalArgumentException("Invalid admin details"));
        ResponseEntity<?> responseEntity = adminController.updateAdmin(id, new User());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    void testDeleteAdminByUsername_Success() {
        String username = "adminUsername";
        when(adminService.deleteAdminByUsername(username)).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<?> responseEntity = adminController.deleteAdminByUsername(username);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testDeleteAdminByUsername_InternalServerError() {
        String username = "adminUsername";
        when(adminService.deleteAdminByUsername(username)).thenThrow(new RuntimeException("Internal Server Error"));
        ResponseEntity<?> responseEntity = adminController.deleteAdminByUsername(username);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void testGetAllAdmins_Success() {
        List<User> admins = Arrays.asList(new User(), new User());
        when(adminService.getAllAdmins()).thenReturn(admins);
        ResponseEntity<?> responseEntity = adminController.getAllAdmins();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(admins, responseEntity.getBody());
    }

    @Test
    void testGetAllAdmins_InternalServerError() {
        when(adminService.getAllAdmins()).thenThrow(new RuntimeException("Internal Server Error"));
        ResponseEntity<?> responseEntity = adminController.getAllAdmins();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}

