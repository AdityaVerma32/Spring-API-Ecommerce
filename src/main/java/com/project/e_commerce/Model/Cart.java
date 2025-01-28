package com.project.e_commerce.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id" , nullable = false)
    @JsonIgnoreProperties({"password","email","role","firstName","lastName","createdAt","updatedAt"})
    private Users user;

    @Column(nullable = true)
    private BigDecimal totalPrice;

    @Column(nullable = true)
    private Integer mapToOrder;

    @Column(nullable = true)
    private Integer orderId;

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

    public Cart() {
    }

    public Cart(Users user, BigDecimal totalPrice) {
        this.user = user;
        this.totalPrice = totalPrice;
    }

    public Integer getMapToOrder() {
        return mapToOrder;
    }

    public void setMapToOrder(Integer mapToOrder) {
        this.mapToOrder = mapToOrder;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
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

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", user=" + user +
                ", totalPrice=" + totalPrice +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
