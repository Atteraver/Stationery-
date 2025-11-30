package com.stationery.service;

import com.stationery.dto.LoginRequest;
import com.stationery.dto.RegisterRequest;
import com.stationery.entity.User;
import com.stationery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Req 1: Register
    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists.");
        }

        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // HASH PASSWORD
                .fullName(request.getFullName())
                .role(request.getRole())
                .maxPurchaseLimit(request.getMaxPurchaseLimit() != null ? request.getMaxPurchaseLimit() : 0.0)
                .build();
        
        User savedUser = userRepository.save(newUser);
        
        // Req 11: Email Notification
        emailService.sendNotification(savedUser.getEmail(), 
                                      "Welcome to Stationery System", 
                                      "Your account has been registered successfully. Role: " + savedUser.getRole());
        
        return savedUser;
    }

    // Req 1: Login
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials.");
        }
        
        // In a real application, this method generates and returns a JWT token.
        // For this example, we return a simple identifier string.
        return "Login successful. Token placeholder. User ID: " + user.getId() + ", Role: " + user.getRole();
    }
    
    // Req 1: Change Password
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect old password.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);


        // Req 11: Email Notification
        emailService.sendNotification(user.getEmail(), 
                                      "Password Changed", 
                                      "Your password was successfully updated.");
    }

    // Placeholder method to extract user ID from the security context (or token)
    public Long extractUserIdFromToken(String token) {
        // Implement JWT decoding logic here to get the User ID
        // For demonstration, we'll assume a fixed ID or mock logic
        return 2L; // Mock ID for testing purposes (e.g., the Lecturer)
    }
}