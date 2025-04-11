package com.example.driveme.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.driveme.model.User;
import com.example.driveme.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) { // Constructor-based Injection
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserByFullName(@PathVariable("id") Integer id) {
        return userRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/add-vehicle")
    public ResponseEntity<User> addVehicle(@PathVariable("id") Integer Id, @RequestBody User user) {
        return userRepository.findById(Id)
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