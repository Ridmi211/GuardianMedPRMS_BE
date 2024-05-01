package com.guardianMed.patientRecordManagement.system.security;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.guardianMed.patientRecordManagement.system.controllers.AuthController;
import com.guardianMed.patientRecordManagement.system.models.User;
import com.guardianMed.patientRecordManagement.system.repositories.UserRepository;
import com.guardianMed.patientRecordManagement.system.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtpService {
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOtp(String otp, String destination) {
        String subject = "Your One-Time Password (OTP) for Login";
        String message = "Your OTP for login is: " + otp + ".\n\nPlease use this OTP to complete the login process. This OTP is valid for 2 minutes.\n\nIf you did not request this OTP, please contact us immediately to secure your account.\n\nBest regards,\nThe GuardianMed Team";
        emailService.sendEmail(destination, subject, message);
    }
    public boolean validateOtp(String username, String otp) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String userOtp = user.getOtp();
        if (userOtp == null) {
            logger.info("OTP is not set for the user");
            return false;
        }

        if (!userOtp.equals(otp)) {
            logger.info("OTP does not match");
            return false;
        }

        Date otpExpiryTime = user.getOtpExpiryTime();
        if (otpExpiryTime == null || otpExpiryTime.before(new Date())) {
            return false;
        }
        logger.info("OTP verified");
        return true;
    }

}
