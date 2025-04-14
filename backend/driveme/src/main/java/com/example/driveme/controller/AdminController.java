package com.example.driveme.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.driveme.model.Booking;
import com.example.driveme.model.Driver;
import com.example.driveme.model.User;
import com.example.driveme.repository.BookingRepository;
import com.example.driveme.repository.DriverRepository;
import com.example.driveme.repository.UserRepository;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getStats() {
        long totalUsers = userRepository.count();
        long totalDrivers = driverRepository.count();
        long totalBookings = bookingRepository.count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalDrivers", totalDrivers);
        stats.put("totalBookings", totalBookings);
        
        return ResponseEntity.ok(stats);
    }

    // 1. Get all users with detailed information
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getUsers() {
        List<User> users = userRepository.findAll();
        
        List<Map<String, Object>> userDetails = users.stream().map(user -> {
            Map<String, Object> details = new HashMap<>();
            details.put("userId", user.getUserId());
            details.put("fullName", user.getFullName());
            details.put("email", user.getEmail());
            details.put("phone", user.getPhone());
            details.put("aadharCard", user.getAadharCard());
            details.put("averageRating", user.getAverageRating());
            details.put("status", user.getAccountStatus());
            details.put("totalBookings", user.getBookings().size());
            details.put("createdAt", user.getCreatedAt());
            
            return details;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(userDetails);
    }

    // 2. Get all drivers with detailed information
    @GetMapping("/drivers")
    public ResponseEntity<List<Map<String, Object>>> getDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        
        List<Map<String, Object>> driverDetails = drivers.stream().map(driver -> {
            Map<String, Object> details = new HashMap<>();
            details.put("driverId", driver.getDriverId());
            details.put("name", driver.getName());
            details.put("email", driver.getEmail());
            details.put("phone", driver.getPhone());
            details.put("aadharCard", driver.getAadharCard());
            details.put("licenseNumber", driver.getLicenseNumber());
            details.put("status", driver.getStatus());
            details.put("accountStatus", driver.getAccountStatus());
            details.put("averageRating", driver.getAverageRating());
            details.put("totalTrips", driver.getTotalTrips());
            details.put("createdAt", driver.getCreatedAt());
            
            return details;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(driverDetails);
    }

    // 3 & 4. Update user details including status
    @PutMapping("/update-user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
            }
            
            User user = userOpt.get();
            
            // Update fields if present in request
            if (request.containsKey("fullName")) {
                user.setFullName((String) request.get("fullName"));
            }
            
            if (request.containsKey("email")) {
                user.setEmail((String) request.get("email"));
            }
            
            if (request.containsKey("phone")) {
                user.setPhone((String) request.get("phone"));
            }
            
            if (request.containsKey("aadharCard")) {
                user.setAadharCard((String) request.get("aadharCard"));
            }
            
            if (request.containsKey("accountStatus")) {
                String status = (String) request.get("accountStatus");
                try {
                    User.AccountStatus accountStatus = User.AccountStatus.valueOf(status.toUpperCase());
                    user.setAccountStatus(accountStatus);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid account status: " + status));
                }
            }
            
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of(
                "message", "User updated successfully",
                "userId", user.getUserId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update user: " + e.getMessage()));
        }
    }

    // 3 & 4. Update driver details including status
    @PutMapping("/update-driver/{id}")
    public ResponseEntity<?> updateDriver(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<Driver> driverOpt = driverRepository.findById(id);
            if (!driverOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Driver not found"));
            }
            
            Driver driver = driverOpt.get();
            
            // Update fields if present in request
            if (request.containsKey("name")) {
                driver.setName((String) request.get("name"));
            }
            
            if (request.containsKey("email")) {
                driver.setEmail((String) request.get("email"));
            }
            
            if (request.containsKey("phone")) {
                driver.setPhone((String) request.get("phone"));
            }
            
            if (request.containsKey("aadharCard")) {
                driver.setAadharCard((String) request.get("aadharCard"));
            }
            
            if (request.containsKey("licenseNumber")) {
                driver.setLicenseNumber((String) request.get("licenseNumber"));
            }
            
            if (request.containsKey("status")) {
                String status = (String) request.get("status");
                try {
                    Driver.DriverStatus driverStatus = Driver.DriverStatus.valueOf(status.toUpperCase());
                    driver.setStatus(driverStatus);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid driver status: " + status));
                }
            }
            
            if (request.containsKey("accountStatus")) {
                String status = (String) request.get("accountStatus");
                try {
                    Driver.AccountStatus accountStatus = Driver.AccountStatus.valueOf(status.toUpperCase());
                    driver.setAccountStatus(accountStatus);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid account status: " + status));
                }
            }
            
            driverRepository.save(driver);
            
            return ResponseEntity.ok(Map.of(
                "message", "Driver updated successfully",
                "driverId", driver.getDriverId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update driver: " + e.getMessage()));
        }
    }

    // 4. Delete a driver
    @DeleteMapping("/delete-driver/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id) {
        try {
            Optional<Driver> driverOpt = driverRepository.findById(id);
            if (!driverOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Driver not found"));
            }
            
            driverRepository.deleteById(id);
            
            return ResponseEntity.ok(Map.of(
                "message", "Driver deleted successfully",
                "driverId", id
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete driver: " + e.getMessage()));
        }
    }

    // 4. Delete a user
    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
            }
            
            userRepository.deleteById(id);
            
            return ResponseEntity.ok(Map.of(
                "message", "User deleted successfully",
                "userId", id
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete user: " + e.getMessage()));
        }
    }
    
    // 5. View all bookings with ratings and status
    @GetMapping("/bookings")
    public ResponseEntity<List<Map<String, Object>>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        
        List<Map<String, Object>> bookingDetails = bookings.stream().map(booking -> {
            Map<String, Object> details = new HashMap<>();
            details.put("bookingId", booking.getBookingId());
            details.put("pickupLocation", booking.getPickupLocation());
            details.put("dropoffLocation", booking.getDropoffLocation());
            details.put("status", booking.getStatus());
            details.put("createdAt", booking.getCreatedAt());
            details.put("fare", booking.getFare());
            
            // Add ratings if available
            if (booking.getCustomerRating() != null) {
                details.put("driverRating", booking.getDriverRating());
            }
            
            if (booking.getDriverRating() != null) {
                details.put("userRating", booking.getCustomerRating());
            }
            
            // Add user and driver info
            if (booking.getCustomer() != null) {
                details.put("customerId", booking.getCustomer().getUserId());
                details.put("customerName", booking.getCustomer().getFullName());
            }
            
            if (booking.getDriver() != null) {
                details.put("driverId", booking.getDriver().getDriverId());
                details.put("driverName", booking.getDriver().getName());
            }
            
            return details;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(bookingDetails);
    }
    
    // Get specific booking details
    @GetMapping("/bookings/{id}")
    public ResponseEntity<?> getBookingDetails(@PathVariable Long id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        
        if (!bookingOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Booking not found"));
        }
        
        Booking booking = bookingOpt.get();
        Map<String, Object> details = new HashMap<>();
        
        details.put("bookingId", booking.getBookingId());
        details.put("pickupLocation", booking.getPickupLocation());
        details.put("dropoffLocation", booking.getDropoffLocation());
        details.put("pickupDateTime", booking.getPickupDateTime());
        details.put("status", booking.getStatus());
        details.put("createdAt", booking.getCreatedAt());
        details.put("completedAt", booking.getCompletedAt());
        details.put("fare", booking.getFare());
        details.put("driverRating", booking.getDriverRating());
        details.put("customerRating", booking.getCustomerRating());
        details.put("driverFeedback", booking.getDriverFeedback());
        details.put("customerFeedback", booking.getCustomerFeedback());
        
        // Add customer details
        if (booking.getCustomer() != null) {
            Map<String, Object> customer = new HashMap<>();
            customer.put("id", booking.getCustomer().getUserId());
            customer.put("name", booking.getCustomer().getFullName());
            customer.put("email", booking.getCustomer().getEmail());
            customer.put("phone", booking.getCustomer().getPhone());
            details.put("customer", customer);
        }
        
        // Add driver details
        if (booking.getDriver() != null) {
            Map<String, Object> driver = new HashMap<>();
            driver.put("id", booking.getDriver().getDriverId());
            driver.put("name", booking.getDriver().getName());
            driver.put("email", booking.getDriver().getEmail());
            driver.put("phone", booking.getDriver().getPhone());
            details.put("driver", driver);
        }
        
        // Add vehicle details
        if (booking.getVehicle() != null) {
            Map<String, Object> vehicle = new HashMap<>();
            vehicle.put("id", booking.getVehicle().getVehicleId());
            vehicle.put("model", booking.getVehicle().getModel());
            vehicle.put("registrationNumber", booking.getVehicle().getRegistrationNumber());
            details.put("vehicle", vehicle);
        }
        
        return ResponseEntity.ok(details);
    }
}