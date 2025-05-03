package com.SDOS.driveme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SDOS.driveme.model.User;
import com.SDOS.driveme.model.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUser(User user);
    @SuppressWarnings("null")
    Optional<Vehicle> findById(Long id);
}
