package com.guardianMed.patientRecordManagement.system.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.internet.InternetAddress;


import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    @Async
    public void sendEmail(String recipientEmail, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("testingcodefield@gmail.com");

        try {
            javaMailSender.send(mailMessage);
            logger.info("Email sent successfully to: {}", recipientEmail);
        } catch (MailException e) {
            logger.error("Failed to send email to: {}", recipientEmail, e);
            // You can handle the failure here, such as sending a notification or taking alternative actions.
            if (e instanceof MailSendException) {
                MailSendException mailSendException = (MailSendException) e;
                Map<Object, Exception> failedMessages = mailSendException.getFailedMessages();
                for (Object address : failedMessages.keySet()) {
                    logger.error("Failed to send email to invalid address: {}", ((InternetAddress) address).getAddress());
                }
            } else if (e instanceof MailAuthenticationException) {
                // Handle authentication failure
                logger.error("Email authentication failed");
            } else {
                // Handle other exceptions (including connection failures)
                logger.error("Failed to send email", e);
            }
        }
    }
}