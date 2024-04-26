package com.guardianMed.patientRecordManagement.system.security;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.guardianMed.patientRecordManagement.system.models.User;
import com.guardianMed.patientRecordManagement.system.repositories.UserRepository;
import com.guardianMed.patientRecordManagement.system.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    public String generateOtp() {
        // Generate a 6-digit OTP
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);

//        // Calculate OTP expiry time (e.g., 5 minutes from now)
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MINUTE, 5); // 5 minutes expiry time
//        Date otpExpiryTime = cal.getTime();

        return String.valueOf(otp);

    }

    public void sendOtp(String otp, String destination) {
        // Construct email message
        String subject = "Your One-Time Password (OTP) for Login";
        String message = "Your OTP for login is: " + otp + ".\n\nPlease use this OTP to complete the login process. This OTP is valid for 2 minutes.\n\nIf you did not request this OTP, please contact us immediately to secure your account.\n\nBest regards,\nThe GuardianMed Team";


        // Send OTP via email
        emailService.sendEmail(destination, subject, message);
    }

    public boolean validateOtp(String username, String otp) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if OTP matches

        String userOtp = user.getOtp();
        if (userOtp == null) {
            return false; // OTP is not set for the user
        }

        // Check if OTP matches
        if (!userOtp.equals(otp)) {
            return false; // OTP does not match
        }

        // Check if OTP is expired
        Date otpExpiryTime = user.getOtpExpiryTime();
        if (otpExpiryTime == null || otpExpiryTime.before(new Date())) {
            return false;
        }
        return true;
    }

}
