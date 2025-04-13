package com.example.driveme.controller;

import org.springframework.web.bind.annotation.*;

import com.example.driveme.model.Driver;
import com.example.driveme.repository.DriverRepository;
import com.example.driveme.DTO.RegisterResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/driver")
public class DriverController {
    private final DriverRepository driverRepository;

    public DriverController(DriverRepository driverRepository) { // Constructor-based Injection
        this.driverRepository = driverRepository;
    }

    @GetMapping
    public List<RegisterResponseDTO> getAllUsers() {
        List<Driver> data = driverRepository.findAll();

        // Return the required data only
        return data.stream()
                .map(driver -> new RegisterResponseDTO(driver.getDriver_id(), driver.getName(), driver.getEmail(), driver.getPhone(), driver.getAadhar_card()))
                .toList();

    }

}
