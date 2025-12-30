package com.alwon.pos.auth.service;

import com.alwon.pos.auth.model.Operator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JWT token generation and validation service
 */
@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:28800000}") // 8 hours default
    private long jwtExpiration;

    /**
     * Create JWT token for operator
     */
    public String createToken(Operator operator) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("operatorId", operator.getId());
        claims.put("username", operator.getUsername());
        claims.put("role", operator.getRole().name());
        claims.put("email", operator.getEmail());

        String jti = UUID.randomUUID().toString();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(operator.getId().toString())
                .setId(jti)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract operator ID from token
     */
    public Long extractOperatorId(String token) {
        Claims claims = extractAllClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extract JTI (JWT ID) from token
     */
    public String extractJti(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getId();
    }

    /**
     * Validate token
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(claims);
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if token is expired
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
