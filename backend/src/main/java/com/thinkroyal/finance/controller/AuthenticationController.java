package com.thinkroyal.finance.controller;

import com.thinkroyal.finance.dto.LoginRequest;
import com.thinkroyal.finance.dto.LoginResponse;
import com.thinkroyal.finance.dto.UserRegistrationRequest;
import com.thinkroyal.finance.model.User;
import com.thinkroyal.finance.security.JwtUtility;
import com.thinkroyal.finance.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;
    private final JwtUtility jwtUtility;

    public AuthenticationController(UserService userService, JwtUtility jwtUtility) {
        this.userService = userService;
        this.jwtUtility = jwtUtility;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.authenticateUser(request.getUsernameOrEmail(), request.getPassword());
        String token = jwtUtility.generateToken(user.getId());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody UserRegistrationRequest request) {
        User user = userService.registerUser(request);
        String token = jwtUtility.generateToken(user.getId());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
