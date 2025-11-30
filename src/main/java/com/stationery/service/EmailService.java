package com.stationery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // The @Async annotation ensures this method runs on a separate thread.
    // This is crucial for performance as email sending can be slow.
    @Async 
    public void sendNotification(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        // Ensure you have set the 'from' address in application.properties
        // Example: spring.mail.username=your-email@gmail.com
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        
        try {
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            // Log the error but don't stop the main transaction
            System.err.println("--- FAILED TO SEND EMAIL ---");
            System.err.println("Recipient: " + toEmail);
            System.err.println("Error: " + e.getMessage());
        }
    }
}