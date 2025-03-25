package com.example.driveme.repository;

import com.example.driveme.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "vehicles")
    Optional<User> findByEmail(String email);
    Optional<User> findByFullName(String full_name);
}
