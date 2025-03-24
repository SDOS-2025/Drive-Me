package com.example.driveme.controller;

import com.example.driveme.model.User;
import com.example.driveme.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) { // Constructor-based Injection
        this.userRepository = userRepository;
    }

    @GetMapping("/{full_name}")
    public ResponseEntity<User> getUserByFullName(@PathVariable("full_name") String full_name) {
        return userRepository.findByFullName(full_name)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
