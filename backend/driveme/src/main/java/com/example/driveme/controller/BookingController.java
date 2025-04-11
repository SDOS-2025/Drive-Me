package com.example.driveme.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}