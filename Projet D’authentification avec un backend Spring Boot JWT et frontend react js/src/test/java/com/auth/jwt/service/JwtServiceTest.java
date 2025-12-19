package com.auth.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private String secretKey = "test_secret_key_that_is_at_least_32_characters_long_for_hmac_sha256";
    private long expirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "expirationMs", expirationMs);
    }

    @Test
    void testGenerateToken_ShouldCreateTokenWithCorrectClaims() {
        // Arrange
        String username = "testuser";

        // Act
        String token = jwtService.generateToken(username);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Parse token to verify claims
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(username, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        
        // Verify expiration is approximately 1 hour from now
        long expectedExpiration = System.currentTimeMillis() + expirationMs;
        long actualExpiration = claims.getExpiration().getTime();
        assertTrue(Math.abs(expectedExpiration - actualExpiration) < 5000); // Within 5 seconds
    }

    @Test
    void testIsTokenValid_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username);

        // Act
        boolean isValid = jwtService.isTokenValid(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_WithExpiredToken_ShouldReturnFalse() {
        // Arrange
        String username = "testuser";
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 1000); // 1 second ago

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
                .subject(username)
                .issuedAt(new Date(now.getTime() - 2000))
                .expiration(expiredDate)
                .signWith(key)
                .compact();

        // Act
        boolean isValid = jwtService.isTokenValid(expiredToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_WithInvalidSignature_ShouldReturnFalse() {
        // Arrange
        String username = "testuser";
        String differentSecretKey = "different_secret_key_that_is_at_least_32_characters_long_for_hmac";
        
        SecretKey wrongKey = Keys.hmacShaKeyFor(differentSecretKey.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        
        String tokenWithWrongSignature = Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(wrongKey)
                .compact();

        // Act
        boolean isValid = jwtService.isTokenValid(tokenWithWrongSignature);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testExtractUsername_ShouldReturnCorrectUsername() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }
}
