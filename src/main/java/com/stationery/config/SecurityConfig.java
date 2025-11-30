package com.stationery.config;

import com.stationery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure the UserDetailsService to load user details from the database
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> (UserDetails) userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
            .authorizeHttpRequests(auth -> auth
                // Module 1: Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                // Module 2: Item availability is public (or EMPLOYEE/MANAGER)
                .requestMatchers(HttpMethod.GET, "/api/items").permitAll() 
                
                // Manager-specific endpoints (Modules 4, 7)
                .requestMatchers("/api/manager/**").hasAuthority("MANAGER")
                
                // User-specific endpoints (Modules 2, 3)
                // General Requests and Eligibility
                .requestMatchers("/api/users/**", "/api/requests/**").hasAnyAuthority("EMPLOYEE", "MANAGER")
                
                .anyRequest().authenticated()
            )
            // Use basic HTTP authentication (or integrate JWT here later)
            .httpBasic(basic -> {}); 

        return http.build();
    }
}