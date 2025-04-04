package com.sdos.driveme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sdos.driveme.model.User;
import com.sdos.driveme.model.Vehicle;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUser(User user);
}
