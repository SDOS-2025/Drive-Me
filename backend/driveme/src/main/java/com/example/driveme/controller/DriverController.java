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
}
