package com.guardianMed.patientRecordManagement.system.controllers;

import com.guardianMed.patientRecordManagement.system.models.Role;
import com.guardianMed.patientRecordManagement.system.payload.requests.LoginRequest;
import com.guardianMed.patientRecordManagement.system.payload.requests.SignupRequest;
import com.guardianMed.patientRecordManagement.system.payload.response.JwtResponse;
import com.guardianMed.patientRecordManagement.system.repositories.RoleRepository;
import com.guardianMed.patientRecordManagement.system.repositories.UserRepository;
import com.guardianMed.patientRecordManagement.system.models.ERole;
import com.guardianMed.patientRecordManagement.system.models.User;
import com.guardianMed.patientRecordManagement.system.payload.response.MessageResponse;
import com.guardianMed.patientRecordManagement.system.services.UserDetailsImpl;
import com.guardianMed.patientRecordManagement.system.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("/signin")
    @CrossOrigin(origins = "http://localhost:4200")
    @RolesAllowed({"", ""})
    public ResponseEntity<?> authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {

        try {
            logger.info("Authentication request received ");
            Authentication authentication = authenticationManager.authenticate(

                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Check if the user has the admin or superadmin role
            if (!roles.contains(ERole.ROLE_ADMIN.toString()) && !roles.contains(ERole.ROLE_SUPER_ADMIN.toString())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You do not have permission to access this system.");
            }

            String jwt = jwtUtils.generateJwtToken(authentication);
            String successMessage = "Successfully signed in as " + userDetails.getUsername();

            JwtResponse response = new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles);
            response.setSuccessMessage(successMessage);

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            logger.error("Invalid username or password provided for authentication", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (AuthenticationException e) {
            logger.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        } catch (Exception e) {
            logger.error("An error occurred during authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PostMapping("/signup")
    @CrossOrigin(origins = "http://localhost:4200")
    @RolesAllowed({"", ""})
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        logger.info("Registration request received for username: {}", signUpRequest.getUsername());

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role patientRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(patientRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "superadmin":
                        Role superAdminRole = roleRepository.findByName(ERole.ROLE_SUPER_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(superAdminRole);
                        break;
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "user":
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                        break;
                    default:
                        throw new RuntimeException("Error: Invalid role provided.");
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        StringBuilder roleMessageBuilder = new StringBuilder();
        for (Role role : roles) {
            roleMessageBuilder.append(role.getName()).append(", ");
        }
        String roleMessage = roleMessageBuilder.toString();
        if (roleMessage.endsWith(", ")) {
            roleMessage = roleMessage.substring(0, roleMessage.length() - 2);
        }
        logger.info("User registered successfully: {}", signUpRequest.getUsername());
        String successMessage = "Registered successfully with the following role(s): " + roleMessage;
        return ResponseEntity.ok(new MessageResponse(successMessage));
    }


//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpServletRequest request) {
//        // Get the authentication token from the request
//        String token = jwtUtils.parseJwt(request);
//
//        // Invalidate the token (add it to the blacklist or perform any necessary actions)
//        jwtUtils.addToBlacklist(token);
//
//        // Return a success message
//        return ResponseEntity.ok(new MessageResponse("Logout successful"));
//    }

}
