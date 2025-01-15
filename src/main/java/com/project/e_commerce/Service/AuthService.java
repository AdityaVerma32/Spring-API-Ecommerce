package com.project.e_commerce.Service;

import com.project.e_commerce.JWT.JWTService;
import com.project.e_commerce.Model.LoginUser;
import com.project.e_commerce.Model.Users;
import com.project.e_commerce.Repo.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private UserRepo userRepo;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    private AuthenticationManager authenticate;
    private JWTService jwtService;
    private EmailService emailService;

    // Constructor to inject dependencies
    public AuthService(UserRepo userRepo, AuthenticationManager authenticate, JWTService jwtService, EmailService emailService) {
        this.userRepo = userRepo;
        this.authenticate = authenticate;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    // Register a new user and save to the database
    public ResponseEntity<String> registerUser(Users user) {
        try {
            // Encrypt the user's password before saving
            user.setPassword(encoder.encode(user.getPassword()));
            Users savedUser = userRepo.save(user);  // Save user to the repository

            emailService.sendRegistrationEmail(savedUser.getEmail(), savedUser.getFirstName());
            return new ResponseEntity<>("Registration Successfully", HttpStatus.OK);
        } catch (Exception e) {
            // Catch any exception and send a bad request response
            return new ResponseEntity<>("Error during registration: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Authenticate and log in the user
    public ResponseEntity<?> loginUser(LoginUser loginUser) {
        try {
            // Authenticate the user with the provided email and password
            Authentication authentication = authenticate.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword())
            );

            if (authentication.isAuthenticated()) {
                // Generate JWT token after successful authentication
                String token = jwtService.generateToken(loginUser.getEmail());

                // Get the user's role
                String role = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .findFirst()
                        .orElse("UNKNOWN");

                // Prepare the response with the token and role
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("role", role);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                // If authentication fails, return an unauthorized response
                return new ResponseEntity<>("Unauthorized Access", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            // Catch any authentication-related exceptions and return an error response
            return new ResponseEntity<>("Authentication error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Check if the user exists and update their details or create a new user
    public void saveOrUpdateUser(String email, String name) {
        try {
            // Look for the user in the repository by email
            Users user = userRepo.findByEmail(email);

            // If user does not exist, create a new one
            if (user == null) {
                user = new Users();
                user.setEmail(email);
                user.setFirstName(name);
                user.setRole("USER"); // Default role
                userRepo.save(user);  // Save new user
            }
        } catch (Exception e) {
            // Catch any database-related exceptions and log the error
            System.err.println("Error while saving or updating user: " + e.getMessage());
        }
    }
}
