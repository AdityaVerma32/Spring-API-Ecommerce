package com.project.e_commerce.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    private final String key;  // The key used to sign and validate JWTs.

    // Constructor that generates a new key for signing the JWT token using HmacSHA256 algorithm
    public JWTService() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");  // Key generator using HmacSHA256 algorithm
        SecretKey sk = keyGen.generateKey();  // Generating a secret key
        key = Base64.getEncoder().encodeToString(sk.getEncoded());  // Encoding the key in Base64 format for usage in JWT signing
    }

    // Method to generate a JWT token for a given username
    public String generateToken(String username) {

        Map<String,Object> claims = new HashMap<>();  // Claims store custom data inside the JWT

        // Building the JWT token with subject (username), issued time, expiration time, and signing it using the secret key
        return Jwts.builder()
                .setClaims(claims)  // Claims can include user roles, permissions, etc.
                .setSubject(username)  // Setting the subject (usually the username or user ID)
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Setting the issued time of the token
                .setExpiration(new Date(System.currentTimeMillis()+60*60*1000))  // Setting the expiration time (1 hour from current time)
                .signWith(getKey())  // Signing the token with the secret key
                .compact();  // Compacting the JWT into a string format
    }

    // Helper method to get the signing key (SecretKey) from Base64 encoded string
    protected Key getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(key);  // Decoding the Base64 encoded secret key into byte array
        return Keys.hmacShaKeyFor(keyBytes);  // Creating an HMAC signing key using the decoded byte array
    }

    // Method to extract the username (subject) from a JWT token
    public String getUsernameFromToken(String token) {
        // The subject (sub) claim in the JWT payload represents the username or user identifier
        return extractClaim(token, Claims::getSubject);  // Extracting the subject (username) claim from the token
    }
    // Helper method to extract any claim from the token using a claim resolver (e.g., subject, expiration)
    private <T> T extractClaim(String token, Function<Claims,T> claimResolver) {
        final Claims claims = extractAllClaims(token);  // Extracting all claims (payload) from the token
        return claimResolver.apply(claims);  // Resolving the specific claim using the provided function (e.g., Claims::getSubject)
    }
    // Method to extract the expiration date of the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);  // Extracting the expiration claim from the token
    }

    // Method to extract all claims from the JWT token
    private Claims extractAllClaims(String token) {
        // Parsing the JWT and extracting its claims using the secret key for validation
        return Jwts.parserBuilder()
                .setSigningKey(getKey())  // Setting the signing key to validate the JWT signature
                .build()
                .parseClaimsJws(token).getBody();  // Parsing the JWT and returning the claims body (payload)
    }

    // Method to validate the token by comparing the extracted username with the user details and checking expiration
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = getUsernameFromToken(token);  // Extracting the username from the token
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));  // Token is valid if username matches and it's not expired
    }

    // Method to check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());  // If the expiration date is before the current date, the token is expired
    }
}
