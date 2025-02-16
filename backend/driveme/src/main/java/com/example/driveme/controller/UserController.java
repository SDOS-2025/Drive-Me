package com.example.driveme.controller;

import com.example.driveme.model.User;
import com.example.driveme.repository.FirestoreService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

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
    public UserRecord addUser(@RequestParam String email, @RequestParam String password, @RequestParam String username) throws ExecutionException, InterruptedException, FirebaseAuthException {
        return firestoreService.createUser(email, password, username);
    }

    @GetMapping("/get")
    public UserRecord getUser(@RequestParam String email) throws FirebaseAuthException {
        return firestoreService.checkUserExists(email);
    }

    @GetMapping("/all")
    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        return firestoreService.getAllUsers();
    }
}
