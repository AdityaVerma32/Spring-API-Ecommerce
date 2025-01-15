//package com.project.e_commerce;
//
//import com.project.e_commerce.Service.UniqueEmailValidator;
//import jakarta.validation.Constraint;
//import jakarta.validation.Payload;
//
//import java.lang.annotation.*;
//
//@Documented
//@Constraint(validatedBy = UniqueEmailValidator.class)
//@Retention(RetentionPolicy.RUNTIME)
//@Target({ElementType.FIELD,ElementType.METHOD})
//public @interface UniqueEmail {
//
//    String message() default "Email is already in use";
//    String email() default "";
//
//    Class<?>[] groups() default {};
//
//    Class<? extends Payload>[] payload() default {};
//
//}
