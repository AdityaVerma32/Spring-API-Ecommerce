//package com.project.e_commerce.Service;
//
//import com.project.e_commerce.Repo.UserRepo;
//import com.project.e_commerce.UniqueEmail;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import org.springframework.stereotype.Component;
//
//@Component
//public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail,String> {
//
//    private final UserRepo userRepo;
//
//    public UniqueEmailValidator(UserRepo userRepo) {
//        this.userRepo = userRepo;
//    }
//
//    @Override
//    public void initialize(UniqueEmail constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
//    }
//
//    @Override
//    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
//        // Check if userRepo is null (shouldn't be null if Spring injects properly)
//        if (userRepo == null) {
//            throw new IllegalStateException("UserRepo is not injected into UniqueEmailValidator.");
//        }
//        // Check if email is null
//        if (email == null) {
//            return true; // Valid since @NotNull should handle null validation if required
//        }
//
//        // Validate email uniqueness
//        return !userRepo.existsByEmail(email);
//    }
//}


//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Scanner;
//
//class Main {
//
//    public static void main(String[] args) throws IOException {
//        URL url = new URL("https://api-m.sandbox.paypal.com/v2/payments/authorizations/0VF52814937998046");
//        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//        httpConn.setRequestMethod("GET");
//
//        httpConn.setRequestProperty("Content-Type", "application/json");
//        httpConn.setRequestProperty("Authorization", "Bearer A21AAFs9YK9gWL6Vl6AqeoPtm-nf6JmtPOwAc8kfzHVdeigPEhrOJLCvbeIt3fJ4NKvyZo_iWic7sC3RIQrVUdu7igagcuMVQ");
//
//        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
//                ? httpConn.getInputStream()
//                : httpConn.getErrorStream();
//        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
//        String response = s.hasNext() ? s.next() : "";
//        System.out.println(response);
//    }
//}
