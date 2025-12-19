package com.auth.jwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SecureController {

    /**
     * Secure endpoint that requires authentication.
     * Returns a protected message for authenticated users.
     *
     * @return protected message with username
     */
    @GetMapping("/secure")
    public ResponseEntity<String> getSecureData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok("Welcome " + username + "! This is protected data. You are authenticated.");
    }
}
