package com.bookmyspot.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    // This is our private, secret key.
    // This is a 256-bit (32-byte) key encoded in Base6

    public static final String SECRET="5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    // --- 1. TOKEN CREATION ---

    // Public method to generate a token for a user

    public String generateToken(String username){
        Map<String,Object> claims=new HashMap<>();
        return createToken(claims,username);
    }
    //private helper to build the token
    private String createToken(Map<String,Object>claims,String username){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username) // The "subject" of the token is the user
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // Token is valid for 30 minutes
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Sign it with our secret key
                .compact();
    }

    private Key getSignKey() {
        byte [] keyBytes= Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- 2. TOKEN VALIDATION & READING ---

    // This is the main validation method
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Check if the username in the token matches the user AND if the token is not expired
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Checks if the token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);

    }

    // Extracts the username from the token
   public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    // --- Generic helper methods ---

    // A generic function to extract any "claim" (piece of info) from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // This does the actual parsing of the token to get all its data
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
