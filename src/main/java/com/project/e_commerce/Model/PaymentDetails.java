package com.project.e_commerce.Model;

import com.project.e_commerce.Enums.OrderStatus;
import com.project.e_commerce.Enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
public class PaymentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String PaymentId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethod PaymentMethod;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus PaymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")  // Join column in PaymentDetails table
    private OrdersTable order;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // This method is called before persisting the entity to the database
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();  // Sets the creation time when the entity is first saved
        this.updatedAt = LocalDateTime.now();  // Sets the update time to the current timestamp
    }

    // This method is called before updating the entity in the database
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();  // Updates the timestamp whenever the entity is updated
    }

    public OrdersTable getOrder() {
        return order;
    }

    public void setOrder(OrdersTable order) {
        this.order = order;
    }

    public PaymentDetails() {
    }

    public PaymentDetails(String paymentId, PaymentMethod paymentMethod, OrderStatus paymentStatus, OrdersTable order) {
        PaymentId = paymentId;
        PaymentMethod = paymentMethod;
        PaymentStatus = paymentStatus;
        this.order = order;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPaymentId() {
        return PaymentId;
    }

    public void setPaymentId(String paymentId) {
        PaymentId = paymentId;
    }

    public PaymentMethod getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        PaymentMethod = paymentMethod;
    }

    public OrderStatus getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(OrderStatus paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
