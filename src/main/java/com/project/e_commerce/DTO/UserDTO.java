package com.project.e_commerce.DTO;

import jakarta.persistence.Column;

public class UserDTO {

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;

    public UserDTO(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public UserDTO() {
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
}
