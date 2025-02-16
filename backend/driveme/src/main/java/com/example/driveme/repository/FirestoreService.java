package com.example.driveme.repository;

import com.example.driveme.model.User;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class FirestoreService {

    private final Firestore firestore;

    public FirestoreService(Firestore firestore) {
        this.firestore = firestore;
    }

    public String saveUser(String userId, String name, String email) throws ExecutionException, InterruptedException {
        User user = new User(userId, name, email);
        firestore.collection("users").document(userId).set(user).get();
        return "User with ID: " + userId + " has been added";
    }

    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        return firestore.collection("users").get().get().getDocuments()
                .stream()
                .map(doc -> doc.toObject(User.class))  // Convert Firestore document to a User DTO
                .collect(Collectors.toList());
    }
}
