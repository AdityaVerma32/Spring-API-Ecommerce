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
