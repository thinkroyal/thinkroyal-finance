package com.thinkroyal.finance.service;

import com.thinkroyal.finance.dto.UserRegistrationRequest;
import com.thinkroyal.finance.model.User;
import com.thinkroyal.finance.repository.UserRepository;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }

    //TODO: add removeUser() to delete accounts

    public User authenticateUser(String usernameOrEmail, String rawPassword) {

        User user = userRepository.findByEmail(usernameOrEmail)
            .or(() -> userRepository.findByUsername(usernameOrEmail))
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        System.out.println("Checking password for user: " + user.getUsername());
        System.out.println("Stored: " + user.getPassword());
        System.out.println("Match: " + passwordEncoder.matches(rawPassword, user.getPassword()));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return user;
    }
}
