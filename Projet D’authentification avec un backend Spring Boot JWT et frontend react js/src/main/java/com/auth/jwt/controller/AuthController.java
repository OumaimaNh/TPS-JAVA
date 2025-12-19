package com.auth.jwt.controller;

import com.auth.jwt.dto.AuthRequest;
import com.auth.jwt.dto.AuthResponse;
import com.auth.jwt.dto.SignupRequest;
import com.auth.jwt.entity.User;
import com.auth.jwt.repository.UserRepository;
import com.auth.jwt.service.JwtService;
import com.auth.jwt.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * Signup endpoint to create a new user account.
     * Validates the request, checks username uniqueness, and creates the user.
     *
     * @param signupRequest the signup request containing username, email, and password
     * @return success message or error response
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            userService.register(signupRequest.getUsername(), signupRequest.getPassword());
            return ResponseEntity.ok("User created");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Login endpoint to authenticate a user and generate a JWT token.
     * Validates credentials, verifies password, and returns a JWT token.
     *
     * @param authRequest the authentication request containing username and password
     * @return AuthResponse with JWT token or 401 error
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        Optional<User> userOptional = userRepository.findByUsername(authRequest.getUsername());
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        
        User user = userOptional.get();
        
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        
        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
