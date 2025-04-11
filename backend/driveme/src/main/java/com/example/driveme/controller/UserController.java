package com.example.driveme.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.driveme.model.User;
import com.example.driveme.model.Vehicle;
import com.example.driveme.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository) { // Constructor-based Injection
        this.userRepository = userRepository;
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

    @GetMapping("/{id}/vehicles")
    public ResponseEntity<List<Vehicle>> getVehicles(@PathVariable("id") Integer Id) {
        return userRepository.findById(Id)
            .map(user -> ResponseEntity.ok(user.getVehicles()))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<User> getAllUsers() {
            System.out.println(">>> Hit /users endpoint!");
        logger.info("Fetching all users");
        return userRepository.findAll();
    }
}