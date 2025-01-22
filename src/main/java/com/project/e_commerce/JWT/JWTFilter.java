package com.project.e_commerce.JWT;

import com.project.e_commerce.Repo.TokenBlacklistedRepo;
import com.project.e_commerce.Service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private JWTService jwtService;  // Service for handling JWT operations like extraction and validation
    ApplicationContext context;  // Spring application context to access other beans dynamically
    private TokenBlacklistedRepo tokenBlacklistedRepo;  // Repository to check if the token is blacklisted

    // Constructor to initialize the JWTService, ApplicationContext, and TokenBlacklistedRepo
    public JWTFilter(JWTService jwtService, ApplicationContext context, TokenBlacklistedRepo tokenBlacklistedRepo) {
        this.jwtService = jwtService;  // Assigning injected JWT service
        this.context = context;  // Assigning the Spring application context
        this.tokenBlacklistedRepo = tokenBlacklistedRepo;  // Assigning the token blacklist repository
    }

    // Overriding the doFilterInternal method to implement the logic for validating JWT token
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");  // Extracting the Authorization header from the request
        String token = null;  // Variable to store the JWT token
        String username = null;  // Variable to store the username extracted from the token

        System.out.println("Authorization Header:"+authHeader);

        // Checking if the Authorization header exists and starts with "Bearer " indicating a JWT token is present
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);  // Extracting the token part after "Bearer "
            username = jwtService.getUsernameFromToken(token);  // Extracting the username from the token using the JWT service
        }

        // Checking if the token is not blacklisted, the username is not null, and the user is not already authenticated
        if(!tokenBlacklistedRepo.existsByToken(token) && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Dynamically loading the user details from the application context using the username extracted from the token
            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);

            // Validating the token using the JWT service and user details
            if(jwtService.validateToken(token, userDetails)) {

                // Creating an authentication token if the JWT is valid, which will hold the user details and authorities (permissions)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Setting the details of the authentication token, such as the IP address, session details, etc.
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Storing the authentication token in the security context, marking the user as authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Proceeding with the filter chain to allow the request to continue to the next filter or endpoint
        filterChain.doFilter(request, response);
    }
}
