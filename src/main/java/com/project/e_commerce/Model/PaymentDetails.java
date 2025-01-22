package com.project.e_commerce.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PaymentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id")
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

    public PaymentDetails(OrdersTable order) {
        this.order = order;
    }

    public PaymentDetails() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OrdersTable getOrder() {
        return order;
    }

    public void setOrder(OrdersTable order) {
        this.order = order;
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
