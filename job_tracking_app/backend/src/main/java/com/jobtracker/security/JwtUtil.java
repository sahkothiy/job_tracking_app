package com.jobtracker.security;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(String secret) {
        // secret must be at least 32 chars
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email, Long userId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
        .setSubject(email)
        .claim("userId", userId)
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + 1000L * 60 * 60 * 24))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    public Long extractUserId(String token) {
        Object v = getClaims(token).get("userId");
        
        if (v instanceof Integer) return ((Integer) v).longValue();
        
        if (v instanceof Long) return (Long) v;
        
        return Long.parseLong(v.toString());
    }

}
