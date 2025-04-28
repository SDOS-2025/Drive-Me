package com.example.driveme.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/my-profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error fetching user profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user profile: " + e.getMessage()));
        }
    }

@GetMapping("my-profile/profile/{path}")
public ResponseEntity<?> getUserProfilePicture(@PathVariable String path, Authentication authentication) {
    logger.info("Attempting to access profile picture: {}", path);
    
    try {
        // Create the full path and log it for debugging
        Path filePath = Paths.get("backend/driveme/src/main/resources/images/").resolve(path).normalize();
        logger.info("Looking for file at absolute path: {}", filePath.toAbsolutePath());
        
        // Check if directory exists
        Path parentDir = filePath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            logger.error("Parent directory does not exist: {}", parentDir.toAbsolutePath());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Profile picture directory not found"));
        }
        
        // Check if the file exists using Files API
        boolean fileExists = Files.exists(filePath);
        logger.info("File exists check result: {}", fileExists);
        
        if (!fileExists) {
            logger.warn("Profile picture file not found at: {}", filePath.toAbsolutePath());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Profile picture not found"));
        }
        
        // Try to create a resource from the path
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            logger.info("Resource created successfully: {}", resource.getURI());
        } catch (Exception e) {
            logger.error("Error creating resource for path: {}", filePath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error creating resource: " + e.getMessage()));
        }
        
        // Now check if resource exists and is readable
        if (!resource.exists()) {
            logger.warn("Resource exists=false for file that Files API reports as existing");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Profile picture resource not found"));
        }
        
        if (!resource.isReadable()) {
            logger.warn("Resource is not readable: {}", filePath);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Profile picture is not readable"));
        }
        
        // Check file size
        try {
            long fileSize = Files.size(filePath);
            logger.info("File size: {} bytes", fileSize);
            if (fileSize == 0) {
                logger.warn("File exists but is empty");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Profile picture file is empty"));
            }
        } catch (IOException e) {
            logger.error("Could not determine file size", e);
        }
        
        // If the file exists, then check authentication
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Unauthorized access attempt to profile picture: {}", path);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        // File exists and user is authenticated, return the file
        MediaType mediaType = determineMediaType(path);
        logger.info("Serving file with content type: {}", mediaType);
        
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);

    } catch (Exception e) {
        logger.error("Error retrieving profile picture: {}", path, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error retrieving profile picture: " + e.getMessage()));
    }
}

// Helper method to determine media type based on file extension
private MediaType determineMediaType(String filename) {
    String extension = "";
    int i = filename.lastIndexOf('.');
    if (i > 0) {
        extension = filename.substring(i + 1).toLowerCase();
    }
    
    switch (extension) {
        case "png":
            return MediaType.IMAGE_PNG;
        case "jpg":
        case "jpeg":
            return MediaType.IMAGE_JPEG;
        case "gif":
            return MediaType.IMAGE_GIF;
        default:
            return MediaType.APPLICATION_OCTET_STREAM;
    }
}

@PutMapping("/my-profile/update")
public ResponseEntity<?> updateUserProfile(
        @RequestPart(name="userDetails", required = false) Map<String, String> userProfileData,
        @RequestPart(name="profilePicture", required=false) MultipartFile profilePicture,
        Authentication authentication) {
    
    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "User not authenticated"));
    }

    try {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String currentEmail = userDetails.getUsername();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));


        // Extract data from request
        String fullName = userProfileData != null ? userProfileData.get("fullName") : null;
        String email = userProfileData != null ? userProfileData.get("email") : null;
        String phone = userProfileData != null ? userProfileData.get("phone") : null;
        logger.info("Updating user profile for email: {}", currentEmail);
        // Update user data if provided
        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName);
        }

        // If email is changing, check if the new email is already in use
        if (email != null && !email.trim().isEmpty() && !email.equals(currentEmail)) {
            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Email is already in use"));
            }
            user.setEmail(email);
        }

        if (phone != null && !phone.trim().isEmpty()) {
            user.setPhone(phone);
        }

        // Handle profile picture upload if provided
        if (profilePicture != null && !profilePicture.isEmpty()) {
            // Create directory if it doesn't exist
            Path uploadDir = Paths.get("backend/driveme/src/main/resources/images/");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Create a unique filename based on user ID and name
            String filename = user.getUserId() + "_" + user.getFullName().replaceAll("\\s+", "_") + ".jpg";
            Path targetPath = uploadDir.resolve(filename);

            // Save the file
            Files.copy(profilePicture.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Profile picture saved at: {}", targetPath.toAbsolutePath());
            // Read the uploaded image
    BufferedImage originalImage = ImageIO.read(profilePicture.getInputStream());
    if (originalImage != null) {
        // Create a new BufferedImage for the resized image
        BufferedImage resizedImage = new BufferedImage(60, 60, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, 60, 60, null);
        g.dispose(); // Always dispose the Graphics2D object

        // Save the resized image
        ImageIO.write(resizedImage, "jpg", targetPath.toFile());

        logger.info("Profile picture saved at: {}", targetPath.toAbsolutePath());
    } else {
        logger.error("Failed to read uploaded image file");
    }
        }

        // Save the updated user
        User updatedUser = userRepository.save(user);
        
        // Create response DTO without sensitive info
        Map<String, Object> responseUser = new HashMap<>();
        responseUser.put("id", updatedUser.getUserId());
        responseUser.put("fullName", updatedUser.getFullName());
        responseUser.put("email", updatedUser.getEmail());
        responseUser.put("phone", updatedUser.getPhone());

        return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "user", responseUser
        ));

    } catch (Exception e) {
        logger.error("Error updating user profile", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update profile: " + e.getMessage()));
    }
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
                request.getCarNumber());

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
                        user.getUserId());
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
                            user.getUserId()))
                    .toList();

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