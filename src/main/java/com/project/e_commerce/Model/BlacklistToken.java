package com.project.e_commerce.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity  // Marks this class as a JPA entity to be mapped to a database table
public class BlacklistToken {

    @Id  // Indicates that the 'id' field is the primary key for this entity
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Specifies that the ID should be auto-generated by the database
    private Integer id;  // The unique identifier for each record in the table

    private String token;  // The JWT token that is blacklisted

    private LocalDateTime expiration;  // The expiration date and time of the blacklisted token

    // Constructor to create a BlacklistToken with a token and its expiration time
    public BlacklistToken(String token, LocalDateTime expiration) {
        this.token = token;  // Assigning the token
        this.expiration = expiration;  // Assigning the expiration time
    }

    // Getter for the 'id' field
    public Integer getId() {
        return id;  // Returns the ID of the blacklisted token
    }

    // Setter for the 'id' field
    public void setId(Integer id) {
        this.id = id;  // Sets the ID of the blacklisted token
    }

    // Getter for the 'token' field
    public String getToken() {
        return token;  // Returns the blacklisted JWT token
    }

    // Setter for the 'token' field
    public void setToken(String token) {
        this.token = token;  // Sets the blacklisted JWT token
    }

    // Getter for the 'expiration' field
    public LocalDateTime getExpiration() {
        return expiration;  // Returns the expiration time of the blacklisted token
    }

    // Setter for the 'expiration' field
    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;  // Sets the expiration time of the blacklisted token
    }
}
