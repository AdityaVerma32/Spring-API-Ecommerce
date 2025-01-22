package com.project.e_commerce.Controller;

import com.project.e_commerce.Utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation exceptions for invalid method arguments.
     *
     * @param ex MethodArgumentNotValidException containing validation errors.
     * @return ResponseEntity with error messages and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(new ApiResponse<>(false,"Some Error Occurred",errors), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle BadCredentialsException for invalid login attempts.
     *
     * @param ex BadCredentialsException thrown during authentication.
     * @return ResponseEntity with a user-friendly error message and HTTP status UNAUTHORIZED.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false,"Invalid username or password. Please check your credentials and try again.",null));
    }

    /**
     * Handle unexpected exceptions globally.
     *
     * @param ex Exception that is not specifically handled elsewhere.
     * @return ResponseEntity with a generic error message and HTTP status INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex) {
        return new ResponseEntity<>(new ApiResponse(false,"An unexpected Exception Occurred: " + ex.getMessage(),null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
