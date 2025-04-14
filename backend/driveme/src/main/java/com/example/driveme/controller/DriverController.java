package com.example.driveme.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.driveme.DTO.RegisterResponseDTO;
import com.example.driveme.model.Booking;
import com.example.driveme.model.Driver;
import com.example.driveme.repository.BookingRepository;
import com.example.driveme.repository.DriverRepository;

@RestController
@RequestMapping("/driver")
public class DriverController {
    private final DriverRepository driverRepository;
    private final BookingRepository bookingRepository;

    public DriverController(DriverRepository driverRepository, BookingRepository bookingRepository) {
        this.driverRepository = driverRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public List<RegisterResponseDTO> getAllDrivers() {
        List<Driver> data = driverRepository.findAll();

        // Return the required data only
        return data.stream()
                .map(driver -> new RegisterResponseDTO(driver.getDriverId(), driver.getName(), driver.getEmail(), 
                     driver.getPhone(), driver.getAadharCard(), driver.getLicenseNumber()))
                .toList();
    }

    @GetMapping("/status")
    public ResponseEntity<List<Map<String, Object>>> getDriverStatus() {
        List<Driver> drivers = driverRepository.findAll();
        
        List<Map<String, Object>> driverStatusList = drivers.stream().map(driver -> {
            Map<String, Object> status = new HashMap<>();
            status.put("driverId", driver.getDriverId());
            status.put("name", driver.getName());
            status.put("phone", driver.getPhone());
            status.put("status", driver.getStatus());
            
            // Check if driver has any active booking
            List<Booking> activeBookings = bookingRepository.findByDriverAndStatusIn(
                driver, 
                List.of(Booking.BookingStatus.CONFIRMED, Booking.BookingStatus.IN_PROGRESS)
            );
            
            if (!activeBookings.isEmpty()) {
                Booking currentBooking = activeBookings.get(0);
                Map<String, Object> tripDetails = new HashMap<>();
                tripDetails.put("bookingId", currentBooking.getBookingId());
                tripDetails.put("pickupLocation", currentBooking.getPickupLocation());
                tripDetails.put("dropoffLocation", currentBooking.getDropoffLocation());
                tripDetails.put("status", currentBooking.getStatus());
                status.put("currentTrip", tripDetails);
            }
            
            return status;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(driverStatusList);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getDriverById(@PathVariable Long id) {
        Optional<Driver> driver = driverRepository.findById(id);
        
        if (driver.isPresent()) {
            Map<String, Object> driverDetails = new HashMap<>();
            Driver d = driver.get();
            
            driverDetails.put("driverId", d.getDriverId());
            driverDetails.put("name", d.getName());
            driverDetails.put("email", d.getEmail());
            driverDetails.put("phone", d.getPhone());
            driverDetails.put("licenseNumber", d.getLicenseNumber());
            driverDetails.put("status", d.getStatus());
            driverDetails.put("currentLocation", d.getCurrentLocation());
            driverDetails.put("averageRating", d.getAverageRating());
            driverDetails.put("totalTrips", d.getTotalTrips());
            
            return ResponseEntity.ok(driverDetails);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Driver not found"));
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateDriverStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String newStatus = request.get("status");
            if (newStatus == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Status is required"));
            }
            
            Optional<Driver> driverOpt = driverRepository.findById(id);
            if (!driverOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Driver not found"));
            }
            
            Driver driver = driverOpt.get();
            
            try {
                Driver.DriverStatus updatedStatus = Driver.DriverStatus.valueOf(newStatus.toUpperCase());
                driver.setStatus(updatedStatus);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid status value: " + newStatus));
            }
            
            // If location is provided, update it
            String location = request.get("location");
            if (location != null) {
                driver.setCurrentLocation(location);
            }
            
            driverRepository.save(driver);
            
            return ResponseEntity.ok(Map.of(
                "message", "Driver status updated successfully",
                "driverId", driver.getDriverId(),
                "status", driver.getStatus().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update driver status: " + e.getMessage()));
        }
    }
    
    @GetMapping("/my-bookings")
    public ResponseEntity<?> getDriverBookings(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Driver not authenticated"));
        }

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Driver driver = driverRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
                    
            List<Booking> bookings = bookingRepository.findByDriver(driver);
            
            List<Map<String, Object>> bookingSummaries = bookings.stream().map(booking -> {
                Map<String, Object> summary = new HashMap<>();
                summary.put("bookingId", booking.getBookingId());
                summary.put("pickupLocation", booking.getPickupLocation());
                summary.put("dropoffLocation", booking.getDropoffLocation());
                summary.put("status", booking.getStatus().toString());
                summary.put("createdAt", booking.getCreatedAt().toString());
                summary.put("fare", booking.getFare());
                
                if (booking.getCustomer() != null) {
                    summary.put("customerName", booking.getCustomer().getFullName());
                    summary.put("customerPhone", booking.getCustomer().getPhone());
                }
                
                return summary;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(bookingSummaries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch driver bookings: " + e.getMessage()));
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableDrivers() {
        List<Driver> availableDrivers = driverRepository.findByStatus(Driver.DriverStatus.AVAILABLE);
        
        List<Map<String, Object>> driverList = availableDrivers.stream().map(driver -> {
            Map<String, Object> driverInfo = new HashMap<>();
            driverInfo.put("driverId", driver.getDriverId());
            driverInfo.put("name", driver.getName());
            driverInfo.put("phone", driver.getPhone());
            driverInfo.put("currentLocation", driver.getCurrentLocation());
            driverInfo.put("rating", driver.getAverageRating());
            return driverInfo;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(driverList);
    }
}