package com.project.e_commerce.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ExpiredSessionId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String sessionId;

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

    public ExpiredSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public ExpiredSessionId() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
