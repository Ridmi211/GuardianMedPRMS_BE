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
        String subject = "Your OTP for Login";
        String message = "Your OTP is: " + otp;

        // Send OTP via email
        emailService.sendEmail(destination, subject, message);
    }

    public boolean validateOtp(String username, String otp) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if OTP matches
        if (!user.getOtp().equals(otp)) {
            return false;
        }

        // Check if OTP is expired
        Date otpExpiryTime = user.getOtpExpiryTime();
        if (otpExpiryTime == null || otpExpiryTime.before(new Date())) {
            return false;
        }
        return true;
    }

}
