package com.auth.jwt.service;

import com.auth.jwt.entity.User;
import com.auth.jwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Setup is handled by @ExtendWith(MockitoExtension.class)
    }

    @Test
    void testRegister_WithValidCredentials_ShouldCreateUserWithEncryptedPassword() {
        // Arrange
        String username = "testuser";
        String rawPassword = "password123";
        String encryptedPassword = "$2a$10$encrypted";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encryptedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User result = userService.register(username, rawPassword);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(encryptedPassword, result.getPassword());
        verify(userRepository).existsByUsername(username);
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_WithDuplicateUsername_ShouldThrowRuntimeException() {
        // Arrange
        String username = "existinguser";
        String rawPassword = "password123";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(username, rawPassword);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository).existsByUsername(username);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
