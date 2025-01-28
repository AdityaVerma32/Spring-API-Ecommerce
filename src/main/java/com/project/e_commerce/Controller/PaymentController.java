package com.project.e_commerce.Controller;

import com.project.e_commerce.DTO.PaymentDTO;
import com.project.e_commerce.DTO.PaymentSuccessRequest;
import com.project.e_commerce.Service.PaymentService;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payment")
public class PaymentController {

    private PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("saveOrderData")
    public ResponseEntity<?> saveOrderData(@RequestBody PaymentSuccessRequest paymentSuccessRequest) throws StripeException, MessagingException {
        return paymentService.saveOrderData(paymentSuccessRequest);
    }

}
