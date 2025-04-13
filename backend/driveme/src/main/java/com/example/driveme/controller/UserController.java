package com.example.driveme.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.driveme.DTO.VehicleRequestDTO;
import com.example.driveme.DTO.VehicleResponseDTO;
import com.example.driveme.model.User;
import com.example.driveme.model.Vehicle;
import com.example.driveme.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository) { // Constructor-based Injection
        this.userRepository = userRepository;
    }

    @PostMapping("/add-vehicle")
    public ResponseEntity<?> addVehicle(@RequestBody VehicleRequestDTO request) {
        logger.info("Adding vehicle for user with ID: {}", request.getUser_id());

        // Find the user
        Optional<User> userOptional = userRepository.findById(request.getUser_id());

        if (!userOptional.isPresent()) {
            logger.error("User with ID: {} not found", request.getUser_id());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID: " + request.getUser_id() + " not found");
        }

        User user = userOptional.get();

        // Create new vehicle
        Vehicle vehicle = new Vehicle();
        vehicle.setModel(request.getModel());
        vehicle.setRegistration_number(request.getRegistration_number());
        vehicle.setCar_number(request.getCar_number());

        List<Vehicle> vehicles = user.getVehicles();
        if (vehicles == null) {
            vehicles = new ArrayList<>();
        }
        vehicles.add(vehicle);
        vehicle.setUser(user); // Set the user for the vehicle
        user.setVehicles(vehicles); // Set the vehicles list for the user

        // Save updated user
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
                    vehicle.getVehicle_id(), 
                    vehicle.getModel(),
                    vehicle.getRegistration_number(),
                    vehicle.getCar_number(),
                    user.getId()  // just include user ID reference
                );
                allVehicles.add(dto);
            }
        }
        
        return allVehicles;
    }

    @GetMapping
    public List<User> getAllUsers() {
            System.out.println(">>> Hit /users endpoint!");
        logger.info("Fetching all users");
        return userRepository.findAll();
    }
}