package com.project.e_commerce.Service;

import com.project.e_commerce.JWT.JWTService;
import com.project.e_commerce.Model.BlacklistToken;
import com.project.e_commerce.Repo.TokenBlacklistedRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class TokenBlacklistedService {

    private final JWTService jwtService;
    private final TokenBlacklistedRepo tokenBlacklistedRepo;

    public TokenBlacklistedService(JWTService jwtService, TokenBlacklistedRepo tokenBlacklistedRepo) {
        this.jwtService = jwtService;
        this.tokenBlacklistedRepo = tokenBlacklistedRepo;
    }

    /**
     * This method handles user logout by blacklisting the JWT token.
     * The token is extracted from the Authorization header, and its expiration
     * time is used to create a BlacklistToken entry in the database.
     *
     * @param request HTTP request containing the Authorization header.
     * @return ResponseEntity with a success message and appropriate HTTP status.
     */
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        try {
            // Extracting the Authorization header from the request.
            String authHeader = request.getHeader("Authorization");

            // Check if the Authorization header is present and starts with "Bearer ".
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Extract the token from the header by removing "Bearer " prefix.
                String token = authHeader.substring(7);

                // Extract expiration date from the JWT token.
                Date expirationTime = jwtService.extractExpiration(token);

                // Convert the expiration time to LocalDateTime for storage in the database.
                LocalDateTime localExpirationTime = expirationTime.toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();

                // Create a BlacklistToken object with the token and its expiration time.
                BlacklistToken blacklistToken = new BlacklistToken(token, localExpirationTime);

                // Save the BlacklistToken to the database to mark it as blacklisted.
                tokenBlacklistedRepo.save(blacklistToken);

                // Return a successful response.
                return new ResponseEntity<>("Logout Successful", HttpStatus.OK);
            } else {
                // If the Authorization header is missing or malformed, throw an exception.
                throw new BadCredentialsException("Invalid Authorization header format.");
            }
        } catch (BadCredentialsException ex) {
            // Handle invalid authorization header format or missing token.
            return new ResponseEntity<>("Bad credentials: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            // Handle any other exceptions that may occur.
            return new ResponseEntity<>("An error occurred during logout: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
