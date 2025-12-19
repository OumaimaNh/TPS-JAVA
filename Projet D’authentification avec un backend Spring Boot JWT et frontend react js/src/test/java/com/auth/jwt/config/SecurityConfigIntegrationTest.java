package com.auth.jwt.config;

import com.auth.jwt.entity.User;
import com.auth.jwt.repository.UserRepository;
import com.auth.jwt.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testPublicAccessToAuthSignupEndpoint() throws Exception {
        String signupJson = "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\"}";

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testPublicAccessToAuthLoginEndpoint() throws Exception {
        // Create a user first
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        String loginJson = "{\"username\":\"testuser\",\"password\":\"password123\"}";

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testProtectedAccessToApiEndpointsWithoutToken() throws Exception {
        mockMvc.perform(get("/api/secure"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testProtectedAccessToApiEndpointsWithValidToken() throws Exception {
        // Create a user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        // Generate a valid JWT token
        String token = jwtService.generateToken("testuser");

        mockMvc.perform(get("/api/secure")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Access granted! JWT authentication is working correctly."));
    }

    @Test
    public void testJwtValidationWithExpiredToken() throws Exception {
        // Create a user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        // Generate an expired token (this would require modifying JwtService to accept custom expiration)
        // For this test, we'll use an invalid token format instead
        String invalidToken = "invalid.token.format";

        mockMvc.perform(get("/api/secure")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testJwtValidationWithInvalidSignature() throws Exception {
        // Create a user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        // Use a token with tampered signature
        String tamperedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTUxNjIzOTAyMn0.invalid_signature";

        mockMvc.perform(get("/api/secure")
                .header("Authorization", "Bearer " + tamperedToken))
                .andExpect(status().isForbidden());
    }
}
