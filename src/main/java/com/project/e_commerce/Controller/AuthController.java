package com.project.e_commerce.Controller;

import com.project.e_commerce.Model.LoginUser;
import com.project.e_commerce.Model.Users;
import com.project.e_commerce.Service.AuthService;
import com.project.e_commerce.Service.TokenBlacklistedService;
import com.project.e_commerce.Utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistedService tokenBlacklistedService;

    // Constructor for dependency injection
    public AuthController(AuthService authService, TokenBlacklistedService tokenBlacklistedService) {
        this.tokenBlacklistedService = tokenBlacklistedService;
        this.authService = authService;
    }

    /**
     * Register a new user.
     *
     * @param user The user object containing user details.
     * @return ResponseEntity with success or failure message and HTTP status.
     */
    @PostMapping("register")
    public ResponseEntity<ApiResponse<Users>> registerUser(@RequestBody Users user) {
        try {
            return authService.registerUser(user);
        } catch (Exception e) {
            // Handle unexpected exceptions
            return new ResponseEntity<>(new ApiResponse<>(true, "An unexpected error occurred during registration: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Login a user and generate an authentication token.
     *
     * @param loginUser The login object containing username and password.
     * @return ResponseEntity with the generated token or an error message.
     */
    @PostMapping("login")
    public ResponseEntity<ApiResponse<Object>> loginUser(@RequestBody LoginUser loginUser) {
        try {
            return authService.loginUser(loginUser);
        } catch (IllegalArgumentException e) {
            // Handle invalid login details
            return new ResponseEntity<>(new ApiResponse<>(false,"Invalid username or password: " + e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Handle unexpected exceptions
            return new ResponseEntity<>(new ApiResponse<>(false, "An unexpected error occurred during login: " + e.getMessage(),null),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logout a user by blacklisting their authentication token.
     *
     * @param request The HTTP request containing the user's authentication token.
     * @return String message indicating success or failure.
     */
    @PostMapping("logout")
    public ResponseEntity<ApiResponse<Object>> logoutUser(HttpServletRequest request) {
        try {
            return tokenBlacklistedService.logoutUser(request);
        } catch (Exception e) {
            // Log the error and return a meaningful message
            return new ResponseEntity<>(new ApiResponse<>(false,"An unexpected error occurred during logout. Please try again.",null),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
