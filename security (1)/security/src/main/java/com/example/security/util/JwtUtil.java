package com.example.security.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	
	// Inject values from properties
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    
    
    
	    // Generate Access Token
	    public String generateAccessToken(String username) {
	        return Jwts.builder()
	                .setSubject(username)
	                .setIssuedAt(new Date())
	                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
	                .signWith(SignatureAlgorithm.HS256, secretKey)
	                .compact();
	    }

	    // Generate Refresh Token
	    public String generateRefreshToken(String username) {
	        return Jwts.builder()
	                .setSubject(username)
	                .setIssuedAt(new Date())
	                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
	                .signWith(SignatureAlgorithm.HS256, secretKey)
	                .compact();
	    }

	    // Validate token
	    public boolean validateToken(String token) {
	        try {
	            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
	            return true;
	        } catch (JwtException | IllegalArgumentException e) {
	            throw e;
	        }
	    }


}
