package com.sdos.driveme.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sdos.driveme.model.User;
import com.sdos.driveme.repository.UserRepository;

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

    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        // Printing user details for debugging

        System.out.println("Adding user: " + user.getFullName() + ", " + user.getEmail() + ", " + user.getPhone() + ", " + user.getAadharCard() + ", " + user.getPasswordHash());
        return userRepository.save(user);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestBody User user) {
        boolean isValidUser = false;
    
        // Check if email is provided and valid
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            isValidUser = userRepository.findByEmail(user.getEmail()).stream()
                .anyMatch(u -> u.getPasswordHash().equals(user.getPasswordHash()));
        }
    
        // If not valid by email, check by phone
        if (!isValidUser && user.getPhone() != null && !user.getPhone().isEmpty()) {
            isValidUser = userRepository.findByPhone(user.getPhone()).stream()
                .anyMatch(u -> u.getPasswordHash().equals(user.getPasswordHash()));
        }
    
        if (isValidUser) {
            return ResponseEntity.ok("User Logged In");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }
    }

    @PostMapping("/{full_name}/add-vehicle")
    public ResponseEntity<User> addVehicle(@PathVariable("full_name") String full_name, @RequestBody User user) {
        return userRepository.findByFullName(full_name)
            .map(u -> {
                u.getVehicles().addAll(user.getVehicles());
                return ResponseEntity.ok(userRepository.save(u));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}