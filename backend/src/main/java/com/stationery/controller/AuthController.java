package com.stationery.controller;

import com.stationery.dto.LoginRequest;
import com.stationery.dto.PasswordChangeRequest;
import com.stationery.dto.RegisterRequest;
import com.stationery.entity.User;
import com.stationery.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /api/auth/register (Module 1)
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok(user);
    }

    // POST /api/auth/login (Module 1)
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest request) {
        // In a real app, this returns a JWT or sets a Session.
        // For simplicity here, AuthService returns a token/role string.
        String response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    // PUT /api/auth/change-password (Module 1)
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request, @RequestHeader("Authorization") String token) {
        // You would typically extract the user ID from the JWT token here
        Long userId = authService.extractUserIdFromToken(token); 
        authService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok("Password changed successfully.");
    }
}