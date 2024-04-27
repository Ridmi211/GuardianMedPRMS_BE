package com.guardianMed.patientRecordManagement.system;

import com.guardianMed.patientRecordManagement.system.controllers.AuthController;
import com.guardianMed.patientRecordManagement.system.models.ERole;
import com.guardianMed.patientRecordManagement.system.models.Role;
import com.guardianMed.patientRecordManagement.system.models.User;
import com.guardianMed.patientRecordManagement.system.payload.requests.LoginRequest;
import com.guardianMed.patientRecordManagement.system.payload.requests.SignupRequest;
import com.guardianMed.patientRecordManagement.system.payload.response.MessageResponse;
import com.guardianMed.patientRecordManagement.system.repositories.RoleRepository;
import com.guardianMed.patientRecordManagement.system.repositories.UserRepository;
import com.guardianMed.patientRecordManagement.system.security.OtpService;
import com.guardianMed.patientRecordManagement.system.security.jwt.JwtUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpService otpService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @Test
    void testAuthenticateUser() {
        // Mocking
        LoginRequest loginRequest = new LoginRequest("testUser", "testPassword");
//        loginRequest.setUsername("testUser");
//        loginRequest.setPassword("testPassword");

        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("testPassword");
        Role userRole = new Role();
        userRole.setName(ERole.ROLE_USER);
// Put the Role object into a Set
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

// Set the roles for the user
        user.setRoles(roles);
        user.setOtp("123456");
        user.setOtpExpiryTime(new Date(System.currentTimeMillis() + 120000));

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(otpService.generateOtp()).thenReturn("123456");
        when(encoder.matches("testPassword", user.getPassword())).thenReturn(true);
        when(userRepository.save(any())).thenReturn(user);
        // Mocking the sendOtp method
        doNothing().when(otpService).sendOtp("123456", "test@example.com");

        // Test
        authController.authenticateUser(loginRequest);
    }

    // Add more tests for other methods as needed

@Test
void testAuthenticateUser_InvalidCredentials() {
    // Create a LoginRequest object with invalid credentials
    LoginRequest loginRequest = new LoginRequest("invalidUser", "invalidPassword");

    // Mocking
    when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

    // Test
    ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);
    assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
}

    @Test
    void testAuthenticateUser_UserNotFound() {
        // Create a LoginRequest object with valid credentials
        LoginRequest loginRequest = new LoginRequest("nonExistingUser", "password");

        // Mocking
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        // Test
        ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        // Additional assertions if needed
    }
//    @Test
//    void testVerifyOtp_Success() {
//        // Mocking
//        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));
//        when(otpService.validateOtp(any(), any())).thenReturn(true);
//        when(encoder.matches(any(), any())).thenReturn(true);
//        when(jwtUtils.generateJwtToken(any())).thenReturn("mockedJWT");
//
//        // Test
//        ResponseEntity<?> responseEntity = authController.verifyOtp(Map.of("username", "testUser", "otp", "123456", "password", "testPassword"));
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        // Additional assertions if needed
//    }
@Test
void testVerifyOtp_Success() {
    // Mocking
    User user = new User();
    user.setUsername("testUser");
    user.setPassword("hashedPassword"); // Assuming the password is hashed
    when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
    when(otpService.validateOtp("testUser", "123456")).thenReturn(true);
    when(encoder.matches("password", "hashedPassword")).thenReturn(true);
    when(jwtUtils.generateJwtToken(any())).thenReturn("mockedJWT");

    // Test
    Map<String, String> otpData = new HashMap<>();
    otpData.put("username", "testUser");
    otpData.put("otp", "123456");
    otpData.put("password", "password");
    ResponseEntity<?> responseEntity = authController.verifyOtp(otpData);

    // Assertions
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    // Add more assertions as needed
}

    @Test
    void testVerifyOtp_InvalidOtpOrPassword() {
        // Mocking
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("hashedPassword"); // Assuming the password is hashed
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(otpService.validateOtp("testUser", "wrongOTP")).thenReturn(false);
        when(encoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        // Test
        Map<String, String> otpData = new HashMap<>();
        otpData.put("username", "testUser");
        otpData.put("otp", "wrongOTP");
        otpData.put("password", "wrongPassword");
        ResponseEntity<?> responseEntity = authController.verifyOtp(otpData);

        // Assertions
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        // Add more assertions as needed
    }
    @Test
    void testVerifyOtp_UserNotFound() {
        // Mocking
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        // Test
        Map<String, String> otpData = new HashMap<>();
        otpData.put("username", "nonExistingUser");
        otpData.put("otp", "123456");
        otpData.put("password", "password");
        ResponseEntity<?> responseEntity = authController.verifyOtp(otpData);

        // Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        // Add more assertions as needed
    }


//    @Test
//    void testVerifyOtp_Failure() {
//        // Mocking
//        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));
//        when(otpService.validateOtp(any(), any())).thenReturn(false);
//
//        // Test
//        ResponseEntity<?> responseEntity = authController.verifyOtp(Map.of("username", "testUser", "otp", "wrongOTP", "password", "testPassword"));
//        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
//        // Additional assertions if needed
//    }

//    @Test
//    void testRegisterUser_Success() {
//        // Create a SignupRequest object with valid data
//        // Create a SignupRequest object with valid data
//        SignupRequest signupRequest = new SignupRequest();
//        signupRequest.setUsername("newUser");
//        signupRequest.setEmail("newUser@example.com");
//        signupRequest.setPassword("password");
//        signupRequest.setRoles(Collections.singleton("admin"));
//
//        // Mocking
//        when(userRepository.existsByUsername(any())).thenReturn(false);
//        when(userRepository.existsByEmail(any())).thenReturn(false);
//        when(roleRepository.findByName(any())).thenReturn(Optional.of(new Role()));
//
//        // Test
//        ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        // Additional assertions if needed
//    }
@Test
void testRegisterUser_Success() {
    // Create a SignupRequest object with valid data
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setUsername("newUser");
    signupRequest.setEmail("newUser@example.com");
    signupRequest.setPassword("password");
    signupRequest.setRoles(Collections.singleton("admin"));
    // Mocking
    when(userRepository.existsByUsername("newUser")).thenReturn(false);
    when(userRepository.existsByEmail("newUser@example.com")).thenReturn(false);
//    when(roleRepository.findByName(any())).thenReturn(Optional.of(new Role()));
    Role userRole = new Role();
    userRole.setName(ERole.ROLE_ADMIN); // Assuming ERole is an enum representing role names
    when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(userRole));


    // Test
    ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);

    // Assertions
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertEquals("Registered successfully with the following role(s): ROLE_ADMIN", ((MessageResponse) responseEntity.getBody()).getMessage());
}

    @Test
    void testRegisterUser_UsernameAlreadyTaken() {
        // Create a SignupRequest object with a username that already exists
//        SignupRequest signupRequest = new SignupRequest("existingUser", "existingUser@example.com", "password", Collections.singleton("ROLE_USER"));
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("existingUser");
        signupRequest.setEmail("existingUser@example.com");
        signupRequest.setPassword("password");
        signupRequest.setRoles(Collections.singleton("admin"));
        // Mocking
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        // Test
        ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Error: Username is already taken!", ((MessageResponse) responseEntity.getBody()).getMessage());
    }
    @Test
    void testRegisterUser_EmailAlreadyInUse() {
        // Create a SignupRequest object with an email that already exists
//        SignupRequest signupRequest = new SignupRequest("newUser", "existingEmail@example.com", "password", Collections.singleton("ROLE_USER"));
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newUser");
        signupRequest.setEmail("existingEmail@example.com");
        signupRequest.setPassword("password");
        signupRequest.setRoles(Collections.singleton("admin"));
        // Mocking
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("existingEmail@example.com")).thenReturn(true);
        // Mocking roleRepository.findByName() method



        // Test
        ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Error: Email is already in use!", ((MessageResponse) responseEntity.getBody()).getMessage());
    }

}