package com.auth.jwt.service;

import com.auth.jwt.entity.User;
import com.auth.jwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        // Setup is handled by @ExtendWith(MockitoExtension.class)
    }

    @Test
    void testLoadUserByUsername_WithExistingUser_ShouldReturnUserDetails() {
        // Arrange
        String username = "testuser";
        String password = "$2a$10$encrypted";
        User user = new User(username, password);
        user.setId(1L);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void testLoadUserByUsername_WithNonExistentUser_ShouldThrowUsernameNotFoundException() {
        // Arrange
        String username = "nonexistent";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });

        assertTrue(exception.getMessage().contains("User not found"));
        assertTrue(exception.getMessage().contains(username));
        verify(userRepository).findByUsername(username);
    }
}
