package com.hostelhive.hostelhive.Controller;

import com.hostelhive.hostelhive.models.User;
import com.hostelhive.hostelhive.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500", "http://localhost:5500"})
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registration endpoints
    @PostMapping("/register-student")
    public ResponseEntity<String> registerStudent(@RequestBody User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_STUDENT");
        userRepo.save(user);
        return ResponseEntity.ok("Student registered successfully");
    }

    @PostMapping("/register-manager")
    public ResponseEntity<String> registerManager(@RequestBody User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_MANAGER");
        userRepo.save(user);
        return ResponseEntity.ok("Manager registered successfully");
    }

    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_ADMIN");
        userRepo.save(user);
        return ResponseEntity.ok("Admin registered successfully");
    }

    // Role-specific login endpoints
    @PostMapping("/login-student")
    public ResponseEntity<String> loginStudent(@RequestBody User user) {
        return authenticateUser(user, "ROLE_STUDENT", "Student");
    }

    @PostMapping("/login-manager")
    public ResponseEntity<String> loginManager(@RequestBody User user) {
        return authenticateUser(user, "ROLE_MANAGER", "Manager");
    }

    @PostMapping("/login-admin")
    public ResponseEntity<String> loginAdmin(@RequestBody User user) {
        return authenticateUser(user, "ROLE_ADMIN", "Admin");
    }

    // Generic login endpoint (optional - for backward compatibility)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        Optional<User> existingUser = userRepo.findByEmail(user.getEmail());
        
        if (existingUser.isPresent() && 
            passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
            return ResponseEntity.ok("Login successful - Role: " + existingUser.get().getRole());
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    // Private helper method for role-specific authentication
    private ResponseEntity<String> authenticateUser(User loginRequest, String expectedRole, String userType) {
        try {
            // Find user by email
            Optional<User> existingUser = userRepo.findByEmail(loginRequest.getEmail());
            
            if (existingUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
            }
            
            User user = existingUser.get();
            
            // Check password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid password");
            }
            
            // Check role
            if (!expectedRole.equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: You don't have " + userType.toLowerCase() + " privileges");
            }
            
            // Successful authentication
            return ResponseEntity.ok(userType + " login successful");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Login failed: " + e.getMessage());
        }
    }
}