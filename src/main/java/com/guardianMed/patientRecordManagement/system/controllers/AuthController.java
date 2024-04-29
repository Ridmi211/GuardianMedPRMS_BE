package com.guardianMed.patientRecordManagement.system.controllers;

import com.guardianMed.patientRecordManagement.system.models.Role;
import com.guardianMed.patientRecordManagement.system.payload.requests.LoginRequest;
import com.guardianMed.patientRecordManagement.system.payload.requests.SignupRequest;
import com.guardianMed.patientRecordManagement.system.payload.response.ApiResponse;
import com.guardianMed.patientRecordManagement.system.payload.response.JwtResponse;
import com.guardianMed.patientRecordManagement.system.payload.response.OTPResponse;
import com.guardianMed.patientRecordManagement.system.repositories.RoleRepository;
import com.guardianMed.patientRecordManagement.system.repositories.UserRepository;
import com.guardianMed.patientRecordManagement.system.models.ERole;
import com.guardianMed.patientRecordManagement.system.models.User;
import com.guardianMed.patientRecordManagement.system.payload.response.MessageResponse;
import com.guardianMed.patientRecordManagement.system.security.OtpService;
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
import org.springframework.security.core.GrantedAuthority;


import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.*;
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
    OtpService otpService;

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

            // Authenticate with username and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Retrieve user entity
            User user = userRepository.findByUsername(loginRequest.username())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate OTP
            String otp = otpService.generateOtp();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 2); // 5 minutes expiry time
            Date otpExpiryTime = cal.getTime();
// Save the OTP to the user entity
            user.setOtp(otp);
            user.setOtpExpiryTime(otpExpiryTime);
            userRepository.save(user);
            otpService.sendOtp(otp,user.getEmail());

            logger.info("OTP sent ");
            return ResponseEntity.ok(new OTPResponse(true, "OTP sent to your email for verification"));
        } catch (BadCredentialsException e) {
            logger.error("Invalid username or password provided for authentication", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, "Invalid username or password"));
        } catch (AuthenticationException e) {
            logger.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        } catch (Exception e) {
            logger.error("An error occurred during authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PostMapping("/verify-otp")
    @CrossOrigin(origins = "http://localhost:4200")
    @RolesAllowed({"", ""})
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> otpData) {

        try {
            String username = otpData.get("username");
            String otp = otpData.get("otp");
            String password = otpData.get("password"); // Retrieve password from request

            // Retrieve user entity
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate OTP
            if (otpService.validateOtp(username, otp) && encoder.matches(password, user.getPassword())) { // Verify password
                // If OTP and password are valid, generate JWT token
                UserDetailsImpl userDetails = UserDetailsImpl.build(user);

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                String jwt = jwtUtils.generateJwtToken(authentication);
                String successMessage = "Successfully signed in as " + userDetails.getUsername();
                logger.info("Successfully signed in as " + userDetails.getUsername());
                JwtResponse response = new JwtResponse(jwt,
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()));
                response.setSuccessMessage(successMessage);

                // Clear OTP from user entity
                user.setOtp(null);
                user.setOtpExpiryTime(null);
                userRepository.save(user);

                // Return JWT token
                return ResponseEntity.ok(response);
            } else {
                // Return unauthorized response if OTP or password is invalid
                logger.error("Invalid OTP or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, "Invalid OTP or password"));
            }

        } catch (Exception e) {
            logger.error("Error occurred during OTP verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "An error occurred"));
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
