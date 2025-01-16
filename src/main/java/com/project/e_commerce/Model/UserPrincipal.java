package com.project.e_commerce.Model;

import org.springframework.security.core.GrantedAuthority;  // Importing interface for granted authorities
import org.springframework.security.core.authority.SimpleGrantedAuthority;  // Importing class for simple granted authorities
import org.springframework.security.core.userdetails.UserDetails;  // Importing the UserDetails interface

import java.util.Collection;  // Importing Collection interface to represent authorities
import java.util.List;  // Importing List class to hold granted authorities

// UserPrincipal implements UserDetails to be used in Spring Security for authentication and authorization
public class UserPrincipal implements UserDetails {

    private Users user;  // This will hold the User entity

    // Constructor to initialize the UserPrincipal with a Users object
    public UserPrincipal(Users user) {
        this.user = user;
    }

    // Overriding getAuthorities() from UserDetails to return the user's role as an authority
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Returning a list containing the user's role as a SimpleGrantedAuthority
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));  // Adds "ROLE_" prefix to role name
    }

    // Overriding getPassword() from UserDetails to return the password of the user
    @Override
    public String getPassword() {
        return user.getPassword();  // Returning password stored in the Users entity
    }

    // Overriding getUsername() from UserDetails to return the email of the user
    @Override
    public String getUsername() {
        return user.getEmail();  // Returning the email as the username
    }

    // The following methods are part of the UserDetails interface but are not overridden here, as we don't need them for this implementation.
    // These methods can be customized as needed:

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Returning true to indicate that the account is not expired
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Returning true to indicate that the account is not locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Returning true to indicate that the credentials are not expired
    }

    @Override
    public boolean isEnabled() {
        return true;  // Returning true to indicate that the user account is enabled
    }

    public Integer getUserId(){
        return user.getId();
    }
}
