package com.guardianMed.patientRecordManagement.system.controllers;

import com.guardianMed.patientRecordManagement.system.exception.NotFoundException;
import com.guardianMed.patientRecordManagement.system.models.User;
import com.guardianMed.patientRecordManagement.system.payload.requests.PasswordResetRequest;
import com.guardianMed.patientRecordManagement.system.services.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
@RolesAllowed({"", ""})

public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private AdminService adminService;

    /**
     * Get an admin by ID
     */

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable("id") String id) {
        try {
            User admin = adminService.getAdminById(id);
            return ResponseEntity.ok(admin);
        } catch (NotFoundException e) {
            logger.error("Admin not found for ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An error occurred while retrieving admin with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve admin");
        }
    }

    /**
     * Get an admin by name
     */

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getAdminByName(@PathVariable("name") String name) {
        try {
            User admin = adminService.getAdminByName(name);
            return ResponseEntity.ok(admin);
        } catch (NotFoundException e) {
            logger.error("Admin not found for name: {}", name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An error occurred while retrieving admin with name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve admin");
        }
    }

    /**
     * Update an admin username and email
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable("id") String id, @RequestBody User adminDetails) {
        try {
            User updatedAdmin = adminService.updateAdmin(id, adminDetails);
            return ResponseEntity.ok(updatedAdmin);
        } catch (NotFoundException e) {
            logger.error("Admin not found for ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Failed to update admin with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An error occurred while updating admin with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update admin");
        }
    }

    /**
     * Reset password
    */

    @PutMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable("id") String id, @RequestBody PasswordResetRequest request) {
        try {
            adminService.resetPassword(id, request.getPreviousPassword(), request.getNewPassword(), request.getConfirmPassword());
            return ResponseEntity.ok("Password reset successfully");
        } catch (NotFoundException e) {
            logger.error("Admin not found for ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request to reset password for admin with ID: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An error occurred while resetting password for admin with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password");
        }
    }

    /**
     * Delete an admin by username
     */

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteAdminByUsername(@PathVariable("username") String username) {
        try {
            ResponseEntity<?> response = adminService.deleteAdminByUsername(username);
            return ResponseEntity.ok().body("Admin with username " + username + " deleted successfully");
        } catch (Exception e) {
            logger.error("An error occurred while deleting admin with username: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete admin");
        }
    }

    /**
     * Get all admins
     */

    @GetMapping("/all")
    public ResponseEntity<?> getAllAdmins() {
        try {
            List<User> admins = adminService.getAllAdmins();
            return ResponseEntity.ok(admins);
        } catch (Exception e) {
            logger.error("An error occurred while retrieving all admins", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve admins");
        }
    }
}
