package com.project.e_commerce.Model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class CartItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

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

    public CartItems(Cart cart, Product product, Integer quantity, BigDecimal price) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public CartItems() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
        return "CartItems{" +
                "id=" + id +
                ", cart=" + cart +
                ", product=" + product +
                ", quantity=" + quantity +
                ", price=" + price +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
