package com.example.driveme.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.driveme.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "vehicles")
    Optional<User> findByEmail(String email);
    Optional<User> findByFullName(String full_name);
    Optional<User> findByPhone(String phone);
}
