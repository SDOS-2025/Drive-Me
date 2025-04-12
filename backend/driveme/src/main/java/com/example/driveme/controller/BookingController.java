package com.example.driveme.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.example.driveme.model.Booking;
import com.example.driveme.model.Driver;
import com.example.driveme.model.User;
import com.example.driveme.model.Vehicle;
import com.example.driveme.repository.BookingRepository;
import com.example.driveme.repository.DriverRepository;
import com.example.driveme.repository.UserRepository;
import com.example.driveme.repository.VehicleRepository;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;

    @SuppressWarnings("unchecked")
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> bookingRequest) {
        try {
            // Extract IDs from the request
            Map<String, Object> customerMap = (Map<String, Object>) bookingRequest.get("customer");
            Map<String, Object> driverMap = (Map<String, Object>) bookingRequest.get("driver");
            Map<String, Object> vehicleMap = (Map<String, Object>) bookingRequest.get("vehicle");
            
            Long customerId = Long.valueOf(customerMap.get("id").toString());
            Long driverId = Long.valueOf(driverMap.get("driver_id").toString());
            Long vehicleId = Long.valueOf(vehicleMap.get("id").toString());
            
            // Fetch entities from repositories
            User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
                
            Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
                
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            
            // Create new Booking
            Booking booking = new Booking();
            booking.setCustomer(customer);
            booking.setDriver(driver);
            booking.setVehicle(vehicle);
            booking.setPickupLocation((String) bookingRequest.get("pickupLocation"));
            booking.setDropoffLocation((String) bookingRequest.get("dropoffLocation"));
            
            // Convert fare to BigDecimal
            Object fareObj = bookingRequest.get("fare");
            BigDecimal fare;
            if (fareObj instanceof Number) {
                fare = BigDecimal.valueOf(((Number) fareObj).doubleValue());
            } else {
                fare = new BigDecimal(fareObj.toString());
            }
            booking.setFare(fare);
            
            // Set status to PENDING
            booking.setStatus(Booking.BookingStatus.PENDING);
            
            // Save booking
            Booking savedBooking = bookingRepository.save(booking);
            
            return ResponseEntity.ok(Map.of(
                "bookingId", savedBooking.getBookingId(),
                "status", "PENDING",
                "message", "Booking created successfully"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create booking: " + e.getMessage()));
        }
    }

    // Add this method to your existing BookingController
    @GetMapping("/user")
    public ResponseEntity<?> getUserBookings(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "User not authenticated"));
        }

        try {
            // Get the current user's ID from the authentication token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            User user = userRepository.findByFullName(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

        // Get all bookings for the user
        List<Booking> bookings = bookingRepository.findByCustomer(user);
        
        // Transform bookings to DTOs
        List<Map<String, Object>> bookingSummaries = bookings.stream().map(booking -> {
            Map<String, Object> summary = new HashMap<>();
            summary.put("bookingId", booking.getBookingId());
            summary.put("pickupLocation", booking.getPickupLocation());
            summary.put("dropoffLocation", booking.getDropoffLocation());
            summary.put("createdAt", booking.getCreatedAt().toString());
            summary.put("status", booking.getStatus().toString());
            summary.put("fare", booking.getFare());
            
            if (booking.getDriver() != null) {
                summary.put("driverName", booking.getDriver().getUsername());
            }
            
            if (booking.getVehicle() != null) {
                summary.put("vehicleType", booking.getVehicle().getVehicle_id());
            }
            
            return summary;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(bookingSummaries);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "Failed to fetch user bookings: " + e.getMessage()));
    }
}

@PutMapping("/{id}/cancel")
public ResponseEntity<?> cancelBooking(@PathVariable Long id, Authentication authentication) {
    try {
        // Get the current user's ID from the authentication token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findByFullName(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get the booking
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Check if the booking belongs to the current user
        if (!booking.getCustomer().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "You are not authorized to cancel this booking"));
        }
        
        // Check if the booking can be cancelled
        if (booking.getStatus() != Booking.BookingStatus.PENDING && 
            booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Cannot cancel booking with status " + booking.getStatus()));
        }
        
        // Update booking status
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        return ResponseEntity.ok(Map.of(
            "message", "Booking successfully cancelled",
            "bookingId", booking.getBookingId()
        ));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "Failed to cancel booking: " + e.getMessage()));
    }
}
}