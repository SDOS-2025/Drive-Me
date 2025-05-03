package com.sdos.driveme.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.sdos.driveme.model.Driver;
import com.sdos.driveme.model.Driver.DriverStatus;

@EnableJpaRepositories
@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    // Finding driver by email and full name
    Optional<Driver> findByEmail(String email);
    Optional<Driver> findByName(String name);
    Optional<Driver> findByPhone(String phone);
    List<Driver> findByStatus(DriverStatus status);
    Optional<Driver> findByAadharCard(String aadharCard);
    Optional<Driver> findByLicenseNumber(String licenseNumber);
}
