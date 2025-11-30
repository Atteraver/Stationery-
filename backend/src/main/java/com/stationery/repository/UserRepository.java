package com.stationery.repository;

import com.stationery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Used for login and checking if a user exists
    Optional<User> findByEmail(String email);
    
    // Custom query to find a user by email, used internally by Spring Security
    Optional<User> findByEmailIgnoreCase(String email);
}