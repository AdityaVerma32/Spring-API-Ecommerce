package com.project.e_commerce.Model;

import jakarta.persistence.*;  // Importing JPA annotations for persistence
import jakarta.validation.constraints.NotBlank;  // Importing validation for non-blank fields
import jakarta.validation.constraints.NotNull;   // Importing validation for non-null fields

import java.math.BigDecimal;  // Importing BigDecimal for the price field
import java.time.LocalDateTime;  // Importing LocalDateTime for timestamps

@Entity  // Marking this class as an entity for persistence in the database
public class Product {

    @Id  // Marking the Id field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment the Id field
    private Integer Id;

    @NotNull(message = "Product Name cannot Be Empty")  // Validation annotation for non-null
    @NotBlank(message = "Product Name cannot Be Empty")  // Validation annotation for non-blank
    private String ProductName;  // Name of the product

    @NotNull(message = "Product Name cannot Be Empty")  // Validation annotation for non-null
    @NotBlank(message = "Product Name cannot Be Empty")  // Validation annotation for non-blank
    private String Description;  // Description of the product

    @NotNull(message = "Product Name cannot Be Empty")  // Validation annotation for non-null
    @NotBlank(message = "Product Name cannot Be Empty")  // Validation annotation for non-blank
    private String productImage;  // Image of the product (could be URL or file path)

    @NotNull(message = "Product Name cannot Be Empty")  // Validation annotation for non-null
    @Column(name = "price", precision = 10, scale = 4)  // Allowing 4 decimal places for the price field
    private BigDecimal price;  // Price of the product

    @NotNull(message = "Product Name cannot Be Empty")  // Validation annotation for non-null
    private Integer stock;  // Stock quantity of the product

    private LocalDateTime createdAt;  // Timestamp for when the product is created
    private LocalDateTime updatedAt;  // Timestamp for when the product is last updated

    // Constructor initializing all fields
    public Product(String productImage, Integer id, String productName, String description, BigDecimal price, Integer stock, LocalDateTime createdAt, LocalDateTime updatedAt) {
        Id = id;
        ProductName = productName;
        Description = description;
        this.price = price;
        this.productImage = productImage;
        this.stock = stock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Default constructor
    public Product() {
    }

    // PrePersist lifecycle method: Called before the entity is persisted (inserted into the database)
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();  // Set createdAt to current timestamp
        this.updatedAt = LocalDateTime.now();  // Set updatedAt to current timestamp
    }

    // PreUpdate lifecycle method: Called before the entity is updated in the database
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();  // Set updatedAt to current timestamp
    }

    // Getter and setter methods for each field
    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
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

    // Override toString method to provide a string representation of the product
    @Override
    public String toString() {
        return "Product{" +
                "Id=" + Id +
                ", ProductName='" + ProductName + '\'' +
                ", Description='" + Description + '\'' +
                ", productImage='" + productImage + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
}
