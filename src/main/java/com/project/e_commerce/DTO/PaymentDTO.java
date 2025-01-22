package com.project.e_commerce.DTO;

import com.project.e_commerce.Enums.PaymentMethod;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class PaymentDTO {

    private Integer orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod payment_method;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public PaymentMethod getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(PaymentMethod payment_method) {
        this.payment_method = payment_method;
    }

    @Override
    public String toString() {
        return "PaymentDTO{" +
                "orderId=" + orderId +
                ", payment_method=" + payment_method +
                '}';
    }
}
