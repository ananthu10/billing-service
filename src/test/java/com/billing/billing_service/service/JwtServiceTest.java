package com.billing.billing_service.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String secretKey = "YXNkZmFzZGZhc2RmYXNkZmFzZGZhc2RmYXNkZmFzZGY="; // base64 encoded 32+ char string
    private final long jwtExpiration = 1000 * 60 * 60; // 1 hour

    private UserDetails testUser;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // Inject secret key and expiration using reflection since @Value doesn't work in plain unit tests
        Field secretKeyField = JwtService.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtService, secretKey);

        Field expirationField = JwtService.class.getDeclaredField("jwtExpiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, jwtExpiration);

        testUser = User.withUsername("testuser").password("password").authorities("ROLE_USER").build();
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void extractClaim_ShouldReturnCorrectClaim() {
        String token = jwtService.generateToken(testUser);

        String subject = jwtService.extractClaim(token, Claims::getSubject);
        assertEquals("testuser", subject);
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(testUser);

        boolean isValid = jwtService.isTokenValid(token, testUser);
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalseForInvalidUsername() {
        String token = jwtService.generateToken(testUser);

        UserDetails wrongUser = User.withUsername("wronguser").password("password").authorities("ROLE_USER").build();

        assertFalse(jwtService.isTokenValid(token, wrongUser));
    }

    @Test
    void getExpirationTime_ShouldReturnConfiguredTime() {
        assertEquals(jwtExpiration, jwtService.getExpirationTime());
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        String token = jwtService.generateToken(testUser);
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        assertTrue(expiration.after(new Date()));
    }
}
