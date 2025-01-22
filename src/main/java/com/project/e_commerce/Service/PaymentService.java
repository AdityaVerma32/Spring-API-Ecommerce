package com.project.e_commerce.Service;

import com.project.e_commerce.DTO.PaymentDTO;
import com.project.e_commerce.Enums.PaymentMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private PaypalService paypalService;

    public PaymentService(PaypalService paypalService) {
        this.paypalService = paypalService;
    }

//    public ResponseEntity<?> makePayment(PaymentDTO paymentDTO) {
//
//        switch(paymentDTO.getPayment_method()){
//            case PaymentMethod.PAYPAL:
//                paypalService.makePayment(paymentDTO.getOrderId());
//                break;
//            case PaymentMethod.STRIPE:
//            case PaymentMethod.CREDIT_CARD:
//            case PaymentMethod.BANK_TRANSFER:
//
//        }
//
//    }
}
