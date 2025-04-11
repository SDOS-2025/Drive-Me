package com.example.driveme.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.driveme.model.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    // Finding driver by email and full name
    Optional<Driver> findByEmail(String email);
    Optional<Driver> findByName(String name);
    Optional<Driver> findByPhone(String phone);
}
