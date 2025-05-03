package com.sdos.driveme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.sdos.driveme.model.User;
import com.sdos.driveme.model.Vehicle;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUser(User user);
    @SuppressWarnings("null")
    Optional<Vehicle> findById(Long id);
}
