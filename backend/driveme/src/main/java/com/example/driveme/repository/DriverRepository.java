package com.example.driveme.repository;

import com.example.driveme.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<User, Long> {}
