package com.auth.jwt.service;

import com.auth.jwt.entity.User;
import com.auth.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        String encryptedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(username, encryptedPassword);
        return userRepository.save(user);
    }
}
