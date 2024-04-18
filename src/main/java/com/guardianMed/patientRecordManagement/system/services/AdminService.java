package com.guardianMed.patientRecordManagement.system.services;

import com.guardianMed.patientRecordManagement.system.exception.NotFoundException;
import com.guardianMed.patientRecordManagement.system.repositories.RoleRepository;
import com.guardianMed.patientRecordManagement.system.repositories.UserRepository;
import com.guardianMed.patientRecordManagement.system.models.ERole;
import com.guardianMed.patientRecordManagement.system.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getAdminById(String id) {
        Optional<User> adminOptional = userRepository.findById(id);

        if (adminOptional.isPresent()) {
            return adminOptional.get();
        } else {
            throw new NotFoundException("Admin not found");
        }
    }

    public User getAdminByName(String name) throws NotFoundException {
        Optional<User> adminOptional = userRepository.findByUsername(name);
        User admin = adminOptional.orElseThrow(() -> new NotFoundException("Admin not found"));
        return admin;
    }

    /**
     * Update an admin by username
     */
    public User updateAdmin(String id, User adminDetails) {
        Optional<User> adminOptional = userRepository.findById(id);

        if (adminOptional.isPresent()) {
            User admin = adminOptional.get();

            // Check if the new username already exists
            if (!admin.getUsername().equals(adminDetails.getUsername()) &&
                    userRepository.existsByUsername(adminDetails.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }

            // Check if the new email already exists
            if (!admin.getEmail().equals(adminDetails.getEmail()) &&
                    userRepository.existsByEmail(adminDetails.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }

            admin.setUsername(adminDetails.getUsername());
            admin.setEmail(adminDetails.getEmail());

            logger.info("Updating admin with ID: {}", admin.getId());
            User updatedAdmin = userRepository.save(admin);
            logger.info("Admin updated successfully");

            return updatedAdmin;
        } else {
            throw new NotFoundException("Admin not found");
        }
    }

    /**
     * Reset password
     */
    public void resetPassword(String adminId, String previousPassword, String newPassword, String confirmPassword) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin not found"));

        // Check if the previous password matches
        if (!passwordEncoder.matches(previousPassword, admin.getPassword())) {
            throw new IllegalArgumentException("Incorrect previous password");
        }

        // Check if the new password and confirmed password match
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New password and confirmed password do not match");
        }

        // Update the admin's password
        String hashedPassword = passwordEncoder.encode(newPassword);
        admin.setPassword(hashedPassword);

        logger.info("Resetting password for admin with ID: {}", admin.getId());
        userRepository.save(admin);
        logger.info("Password reset successful");
    }

    /**
     * Delete an admin by username
     */
    public ResponseEntity<?> deleteAdminByUsername(String username) {
        try {
            User admin = getAdminByName(username);

            // Add a confirmation prompt before deleting the admin
//            if (!confirmDelete()) {
//                return ResponseEntity.ok().body("Deletion cancelled");
//            }

            logger.info("Deleting admin with username: {}", username);
            userRepository.delete(admin);
            logger.info("Admin deleted successfully");

            return ResponseEntity.ok().body("Admin with username " + username + " deleted successfully");
        } catch (NotFoundException e) {
            logger.error("Admin not found: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to delete admin: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete admin");
        }
    }

    // Confirmation prompt
//    private boolean confirmDelete() {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Are you sure you want to delete this admin? (yes/no)");
//        String userInput = scanner.nextLine().trim().toLowerCase();
//        return userInput.equals("yes");
//    }

    public List<User> getAllAdmins() {
        return userRepository.findAllByRoles_Name(String.valueOf(ERole.ROLE_ADMIN));
    }

    public void resetPassword(String id) {
    }
}
