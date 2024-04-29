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
    private LoginRequest loginRequest;

    @Test
    void testAuthenticateUser() {
         LoginRequest loginRequest = new LoginRequest("testUser", "testPassword");
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("testPassword");
        Role userRole = new Role();
        userRole.setName(ERole.ROLE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
        user.setOtp("123456");
        user.setOtpExpiryTime(new Date(System.currentTimeMillis() + 120000));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(otpService.generateOtp()).thenReturn("123456");
        when(encoder.matches("testPassword", user.getPassword())).thenReturn(true);
        when(userRepository.save(any())).thenReturn(user);
        doNothing().when(otpService).sendOtp("123456", "test@example.com");
        authController.authenticateUser(loginRequest);
    }


@Test
void testAuthenticateUser_InvalidCredentials() {
    LoginRequest loginRequest = new LoginRequest("invalidUser", "invalidPassword");
    when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);
    ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);
    assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
}

    @Test
    void testAuthenticateUser_UserNotFound() {

        LoginRequest loginRequest = new LoginRequest("nonExistingUser", "password");
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
           }

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
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
}

    @Test
    void testVerifyOtp_InvalidOtpOrPassword() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("hashedPassword"); // Assuming the password is hashed
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(otpService.validateOtp("testUser", "wrongOTP")).thenReturn(false);
        when(encoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);
        Map<String, String> otpData = new HashMap<>();
        otpData.put("username", "testUser");
        otpData.put("otp", "wrongOTP");
        otpData.put("password", "wrongPassword");
        ResponseEntity<?> responseEntity = authController.verifyOtp(otpData);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

    }
    @Test
    void testVerifyOtp_UserNotFound() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());
        Map<String, String> otpData = new HashMap<>();
        otpData.put("username", "nonExistingUser");
        otpData.put("otp", "123456");
        otpData.put("password", "password");
        ResponseEntity<?> responseEntity = authController.verifyOtp(otpData);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

@Test
void testRegisterUser_Success() {
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setUsername("newUser");
    signupRequest.setEmail("newUser@example.com");
    signupRequest.setPassword("password");
    signupRequest.setRoles(Collections.singleton("admin"));
    when(userRepository.existsByUsername("newUser")).thenReturn(false);
    when(userRepository.existsByEmail("newUser@example.com")).thenReturn(false);
    Role userRole = new Role();
    userRole.setName(ERole.ROLE_ADMIN);
    when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(userRole));
    ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertEquals("Registered successfully with the following role(s): ROLE_ADMIN", ((MessageResponse) responseEntity.getBody()).getMessage());
}

    @Test
    void testRegisterUser_UsernameAlreadyTaken() {
     SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("existingUser");
        signupRequest.setEmail("existingUser@example.com");
        signupRequest.setPassword("password");
        signupRequest.setRoles(Collections.singleton("admin"));
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);
        ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Error: Username is already taken!", ((MessageResponse) responseEntity.getBody()).getMessage());
    }
    @Test
    void testRegisterUser_EmailAlreadyInUse() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newUser");
        signupRequest.setEmail("existingEmail@example.com");
        signupRequest.setPassword("password");
        signupRequest.setRoles(Collections.singleton("admin"));
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("existingEmail@example.com")).thenReturn(true);
        ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Error: Email is already in use!", ((MessageResponse) responseEntity.getBody()).getMessage());
    }

}