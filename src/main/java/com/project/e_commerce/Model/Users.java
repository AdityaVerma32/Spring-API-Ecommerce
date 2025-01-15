package com.project.e_commerce.Model;

import jakarta.persistence.*;  // JPA annotations for entity mapping
import jakarta.validation.constraints.Email;  // For validating email format
import jakarta.validation.constraints.NotBlank;  // For checking non-blank strings
import jakarta.validation.constraints.NotNull;  // For checking non-null values
import jakarta.validation.constraints.Size;  // For size validation on string fields

import java.time.LocalDateTime;  // For handling date and time

@Entity  // Marks the class as a JPA entity, which will be mapped to a database table
public class Users {

    @Id  // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Automatically generates the ID with incremental values
    private Integer id;

    @NotNull(message = "Email is Mandatory")  // Ensures email is not null
    @NotBlank(message = "Email is Mandatory")  // Ensures email is not blank
    @Email(message = "Email should be valid")  // Validates the email format
//    @UniqueEmail(message = "Email is already taken")  // Custom validation for unique email (currently commented out)
    @Size(min = 2, max = 50, message = "Email Should range between 2 to 100")  // Validates email length
    private String email;

    @NotNull(message = "Password is Mandatory")  // Ensures password is not null
    @NotBlank(message = "Password is Mandatory")  // Ensures password is not blank
    private String password;

    @NotNull(message = "Role is Mandatory")  // Ensures role is not null
    @NotBlank(message = "Role is Mandatory")  // Ensures role is not blank
    private String role;

    @NotNull(message = "First Name is Mandatory")  // Ensures first name is not null
    @NotBlank(message = "First Name is Mandatory")  // Ensures first name is not blank
    @Size(min = 2, max = 50, message = "Length Should range between 2 to 50")  // Validates first name length
    private String firstName;

    @NotNull(message = "Last Name is Mandatory")  // Ensures last name is not null
    @NotBlank(message = "Last Name is Mandatory")  // Ensures last name is not blank
    @Size(min = 2, max = 50, message = "Length Should range between 2 to 50")  // Validates last name length
    private String lastName;

    private LocalDateTime createdAt;  // Stores the creation timestamp of the user
    private LocalDateTime updatedAt;  // Stores the last updated timestamp of the user

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

    // Default constructor
    public Users() {
    }

    // Parameterized constructor to initialize a user object with values
    public Users(Integer id, String email, String password, String role, String firstName, String lastName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters for all the fields to allow access and modification of the fields
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    // Override toString method to provide a string representation of the user object
    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
