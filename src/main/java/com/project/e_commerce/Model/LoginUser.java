package com.project.e_commerce.Model;

public class LoginUser {

    private String email;  // The email of the user trying to log in
    private String password;  // The password of the user trying to log in

    // Constructor that initializes the 'email' and 'password' fields
    public LoginUser(String email, String password) {
        this.email = email;  // Assigning the email passed to the constructor
        this.password = password;  // Assigning the password passed to the constructor
    }

    // Getter for the 'email' field
    public String getEmail() {
        return email;  // Returns the email of the user
    }

    // Setter for the 'email' field
    public void setEmail(String email) {
        this.email = email;  // Sets the email of the user
    }

    // Getter for the 'password' field
    public String getPassword() {
        return password;  // Returns the password of the user
    }

    // Setter for the 'password' field
    public void setPassword(String password) {
        this.password = password;  // Sets the password of the user
    }
}
