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
import org.springframework.web.cors.CorsConfigurationSource;

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
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource)) // Enable CORS
            .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
            .authorizeHttpRequests(auth -> auth
                // Module 1: Public endpoints
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                // Module 2: Item availability is public (or EMPLOYEE/MANAGER)
                .requestMatchers(HttpMethod.GET, "/api/items").permitAll()

                // Manager-specific endpoints (Modules 4, 7)
                .requestMatchers("/api/manager/**").hasAuthority("MANAGER")

                // User-specific endpoints (Modules 2, 3)
                // General Requests and Eligibility
                .requestMatchers("/api/users/**").hasAnyAuthority("EMPLOYEE", "MANAGER")
                .requestMatchers("/api/requests/**").permitAll()
                .anyRequest().authenticated()
            )
            // Enable HTTP Basic authentication for testing
            .httpBasic(basic -> {});

        return http.build();
    }
}