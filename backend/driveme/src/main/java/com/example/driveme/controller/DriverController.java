package com.example.driveme.controller;

import com.example.driveme.model.Driver;
import com.example.driveme.repository.DriverRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driver")
public class DriverController {
    private final DriverRepository driverRepository;

    public DriverController(DriverRepository driverRepository) { // Constructor-based Injection
        this.driverRepository = driverRepository;
    }

    @GetMapping
    public List<Driver> getAllUsers() {
        return driverRepository.findAll();
    }

    @PostMapping("/add")
    public Driver addDriver(@RequestBody Driver driver) {
        return driverRepository.save(driver);
    }

    @PostMapping("/verify")
    public String verifyDriver(@RequestBody Driver driver) {
        boolean isValidDriver = driverRepository.findAll().stream()
            .anyMatch(d -> d.getPhone().equals(driver.getPhone()) && d.getPassword_hash().equals(driver.getPassword_hash()));

        if (isValidDriver) {
            return "Driver Logged In";
        } else {
            return "Invalid Credentials";
        }
    }
}
