package com.thinkroyal.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.thinkroyal.finance.dto.UserRegistrationRequest;
import com.thinkroyal.finance.service.UserService;
import com.thinkroyal.finance.model.User;

import jakarta.validation.Valid;

public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        User createdUser = userService.registerUser(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    //TODO add more endpoints
}
