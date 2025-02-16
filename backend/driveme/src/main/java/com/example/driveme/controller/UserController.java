package com.example.driveme.controller;

import com.example.driveme.model.User;
import com.example.driveme.repository.FirestoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private FirestoreService firestoreService;

    @PostMapping("/add")
    public String addUser(@RequestParam String userId, @RequestParam String name, @RequestParam String email) throws ExecutionException, InterruptedException {
        return firestoreService.saveUser(userId, name, email);
    }

    @GetMapping("/all")
    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        return firestoreService.getAllUsers();
    }
}
