package com.example.driveme.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.driveme.DTO.VehicleRequestDTO;
import com.example.driveme.DTO.VehicleResponseDTO;
import com.example.driveme.model.User;
import com.example.driveme.model.Vehicle;
import com.example.driveme.repository.UserRepository;
import com.example.driveme.repository.VehicleRepository;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @PostMapping("/add-vehicle")
    public ResponseEntity<?> addVehicle(@RequestBody VehicleRequestDTO request) {

        // Find the user
        Optional<User> userOptional = userRepository.findById(request.getUserId());

        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID: " + request.getUserId() + " not found");
        }

        User user = userOptional.get();

        // Create new vehicle
        System.out.println("Registration NUmber: " + request.getRegistrationNumber());
        Vehicle vehicle = new Vehicle(
                user,
                request.getModel(),
                request.getRegistrationNumber(),
                request.getCarNumber()
        );

        if (request.getVehicleType() != null) {
            try {
                Vehicle.VehicleType vehicleType = Vehicle.VehicleType.valueOf(request.getVehicleType().toUpperCase());
                vehicle.setVehicleType(vehicleType);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid vehicle type: " + request.getVehicleType());
            }
        }

        user.addVehicle(vehicle);
        userRepository.save(user);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Vehicle added successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/vehicles")
    public List<VehicleResponseDTO> getAllVehicles() {
        logger.info("Fetching all vehicles");
        List<User> users = userRepository.findAll();
        List<VehicleResponseDTO> allVehicles = new ArrayList<>();
        
        for (User user : users) {
            for (Vehicle vehicle : user.getVehicles()) {
                VehicleResponseDTO dto = new VehicleResponseDTO(
                    vehicle.getVehicleId(), 
                    vehicle.getModel(),
                    vehicle.getRegistrationNumber(),
                    vehicle.getCarNumber(),
                    user.getUserId()
                );
                allVehicles.add(dto);
            }
        }
        
        return allVehicles;
    }

    @GetMapping("/my-vehicles")
    public ResponseEntity<?> getUserVehicles(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "User not authenticated"));
        }

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
            List<VehicleResponseDTO> userVehicles = user.getVehicles().stream()
                .map(vehicle -> new VehicleResponseDTO(
                    vehicle.getVehicleId(),
                    vehicle.getModel(),
                    vehicle.getRegistrationNumber(),
                    vehicle.getCarNumber(),
                    user.getUserId()
                )).toList();
                
            return ResponseEntity.ok(userVehicles);
        } catch (Exception e) {
            logger.error("Error fetching user vehicles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch user vehicles: " + e.getMessage()));
        }
    }

    @DeleteMapping("/vehicles/{vehicleId}")
    public ResponseEntity<?> removeVehicle(@PathVariable Long vehicleId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "User not authenticated"));
        }

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
                
            // Verify the vehicle belongs to this user
            if (!vehicle.getUser().getUserId().equals(user.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You do not have permission to delete this vehicle"));
            }
            
            // Remove the vehicle
            user.removeVehicle(vehicle);
            userRepository.save(user);
            vehicleRepository.delete(vehicle);
            
            return ResponseEntity.ok(Map.of("message", "Vehicle removed successfully"));
        } catch (Exception e) {
            logger.error("Error removing vehicle", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to remove vehicle: " + e.getMessage()));
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll();
    }
}