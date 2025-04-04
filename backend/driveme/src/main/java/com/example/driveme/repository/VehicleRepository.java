package com.example.driveme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.driveme.model.User;
import com.example.driveme.model.Vehicle;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUser(User user);
}
