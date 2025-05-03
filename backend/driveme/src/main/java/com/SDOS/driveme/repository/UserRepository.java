package com.sdos.driveme.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.sdos.driveme.model.User;

@EnableJpaRepositories
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "vehicles")
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Integer id);
    Optional<User> findByPhone(String phone);
    Optional<User> findByAadharCard(String aadharCard);
    Optional<User> findByFullName(String fullName);
}
