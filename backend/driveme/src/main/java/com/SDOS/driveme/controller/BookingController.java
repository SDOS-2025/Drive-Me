package com.SDOS.driveme.controller;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.SDOS.driveme.model.Booking;
import com.SDOS.driveme.model.Driver;
import com.SDOS.driveme.model.Payment;
import com.SDOS.driveme.model.User;
import com.SDOS.driveme.model.Vehicle;
import com.SDOS.driveme.model.Booking.BookingStatus;
import com.SDOS.driveme.repository.BookingRepository;
import com.SDOS.driveme.repository.DriverRepository;
import com.SDOS.driveme.repository.UserRepository;
import com.SDOS.driveme.repository.VehicleRepository;

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
    public ResponseEntity<?> createBooking(
            @RequestPart("bookingRequest") Map<String, Object> bookingRequest,
            @RequestPart("paymentScreenshot") MultipartFile paymentScreenshot) {
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
            booking.setPickUpDateTime((String) bookingRequest.get("pickupDateTime"));
            booking.setEstimatedDuration(Integer.valueOf(bookingRequest.get("estimatedDuration").toString()));

            // Save the payment screenshot to a directory
            String uploadDir = "uploads/payment-screenshots/";
            String fileName = UUID.randomUUID().toString() + "_" + paymentScreenshot.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, paymentScreenshot.getBytes());

            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setAmount(new BigDecimal(bookingRequest.get("fare").toString()));
            payment.setPaymentScreenshot(fileName);

            booking.setPayments(List.of(payment));

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
                    "message", "Booking created successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create booking: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserBookings(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
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
                    summary.put("driverName", booking.getDriver().getName());
                }

                if (booking.getVehicle() != null) {
                    summary.put("vehicleModel", booking.getVehicle().getModel());
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

    @GetMapping("/user/confirmed")
    public ResponseEntity<?> getUserConfirmedBookings(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Booking> bookings = bookingRepository.findByCustomerAndStatus(
                    user, Booking.BookingStatus.CONFIRMED);

            List<Map<String, Object>> bookingSummaries = bookings.stream().map(booking -> {
                Map<String, Object> summary = new HashMap<>();
                summary.put("bookingId", booking.getBookingId());
                summary.put("pickupLocation", booking.getPickupLocation());
                summary.put("dropoffLocation", booking.getDropoffLocation());
                summary.put("createdAt", booking.getCreatedAt().toString());
                summary.put("fare", booking.getFare());

                if (booking.getDriver() != null) {
                    summary.put("driverName", booking.getDriver().getName());
                    summary.put("driverPhone", booking.getDriver().getPhone());
                }

                if (booking.getVehicle() != null) {
                    summary.put("vehicleModel", booking.getVehicle().getModel());
                    summary.put("registrationNumber", booking.getVehicle().getRegistrationNumber());
                }

                return summary;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(bookingSummaries);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch confirmed bookings: " + e.getMessage()));
        }
    }

    @GetMapping("/user/cancelled")
    public ResponseEntity<?> getUserCancelledBookings(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Booking> bookings = bookingRepository.findByCustomerAndStatus(
                    user, Booking.BookingStatus.CANCELLED);

            List<Map<String, Object>> bookingSummaries = bookings.stream().map(booking -> {
                Map<String, Object> summary = new HashMap<>();
                summary.put("bookingId", booking.getBookingId());
                summary.put("pickupLocation", booking.getPickupLocation());
                summary.put("dropoffLocation", booking.getDropoffLocation());
                summary.put("createdAt", booking.getCreatedAt().toString());

                return summary;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(bookingSummaries);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch cancelled bookings: " + e.getMessage()));
        }
    }

    @GetMapping("/user/completed")
    public ResponseEntity<?> getUserCompletedBookings(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Booking> bookings = bookingRepository.findByCustomerAndStatus(
                    user, Booking.BookingStatus.COMPLETED);

            List<Map<String, Object>> bookingSummaries = bookings.stream().map(booking -> {
                Map<String, Object> summary = new HashMap<>();
                summary.put("bookingId", booking.getBookingId());
                summary.put("pickupLocation", booking.getPickupLocation());
                summary.put("dropoffLocation", booking.getDropoffLocation());
                summary.put("completedAt", booking.getCompletedAt().toString());
                summary.put("fare", booking.getFare());
                summary.put("driverRating", booking.getDriverRating());

                if (booking.getDriver() != null) {
                    summary.put("driverName", booking.getDriver().getName());
                }

                return summary;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(bookingSummaries);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch completed bookings: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            // Get the current user from authentication
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Get the booking
            Booking booking = bookingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // Check if the booking belongs to the current user
            if (!booking.getCustomer().getUserId().equals(user.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You are not authorized to cancel this booking"));
            }

            // Check if the booking can be cancelled
            if (booking.getStatus() != Booking.BookingStatus.PENDING &&
                    booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cannot cancel booking with status " + booking.getStatus()));
            }

            booking.getDriver().setStatus(Driver.DriverStatus.AVAILABLE); // Set driver status to available
            // Update booking status with reason
            String reason = request.getOrDefault("reason", "Cancelled by user");
            booking.cancelBooking(reason);
            bookingRepository.save(booking);

            return ResponseEntity.ok(Map.of(
                    "message", "Booking successfully cancelled",
                    "bookingId", booking.getBookingId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to cancel booking: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestBody Map<String, String> request,
            Authentication authentication) {
        
        
        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }
        try {
            String newStatus = request.get("status");
            if (newStatus == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Status is required"));
            }

            Booking booking = bookingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            BookingStatus updatedStatus;
            try {
                updatedStatus = Booking.BookingStatus.valueOf(newStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid status value: " + newStatus));
            }

            // Handle completing the trip BEFORE changing the status
            if (updatedStatus == Booking.BookingStatus.COMPLETED) {
                String distanceStr = request.get("distance");
                String feedback = request.get("feedback");
                Double distance = (distanceStr != null) ? Double.parseDouble(distanceStr) : 0.0;
                Integer driverRating = request.get("driverRating") != null ?
                        Integer.parseInt(request.get("driverRating")) : null;
                // Call completeTrip directly while status is still CONFIRMED
                booking.completeTrip(distance);
                booking.setDriverRating(driverRating);
                booking.setDriverFeedback(feedback);

                // Set driver status to available and update metrics
                if (booking.getDriver() != null) {
                    booking.getDriver().setStatus(Driver.DriverStatus.AVAILABLE);
                }

                // Update average ratings
                updateDriverRating(booking.getDriver());
            } else {
                // For other status changes, just update the status
                booking.setStatus(updatedStatus);
            }

            bookingRepository.save(booking);

            return ResponseEntity.ok(Map.of(
                    "message", "Booking status updated successfully",
                    "bookingId", booking.getBookingId(),
                    "status", booking.getStatus().toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update booking status: " + e.getMessage()));
        }
    }

    // Add these helper methods to BookingController
    private void updateDriverRating(Driver driver) {
        if (driver == null)
            return;

        List<Booking> completedBookings = bookingRepository.findByDriverAndStatus(
                driver, Booking.BookingStatus.COMPLETED);

        // Calculate new average rating
        double totalRating = 0;
        int ratedBookingsCount = 0;

        for (Booking booking : completedBookings) {
            if (booking.getDriverRating() != null) {
                totalRating += booking.getDriverRating();
                ratedBookingsCount++;
            }
        }

        if (ratedBookingsCount > 0) {
            double averageRating = totalRating / ratedBookingsCount;
            driver.setAverageRating(averageRating);
            driverRepository.save(driver);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBookings() {
        try {
            List<Booking> bookings = bookingRepository.findAll();
            List<Map<String, Object>> bookingSummaries = bookings.stream().map(booking -> {
                Map<String, Object> summary = new HashMap<>();
                summary.put("bookingId", booking.getBookingId());
                summary.put("pickupLocation", booking.getPickupLocation());
                summary.put("dropoffLocation", booking.getDropoffLocation());
                summary.put("status", booking.getStatus());
                summary.put("createdAt", booking.getCreatedAt().toString());
                summary.put("fare", booking.getFare());

                if (booking.getCustomer() != null) {
                    summary.put("customerName", booking.getCustomer().getFullName());
                }

                if (booking.getDriver() != null) {
                    summary.put("driverName", booking.getDriver().getName());
                }

                if (booking.getVehicle() != null) {
                    summary.put("vehicleModel", booking.getVehicle().getModel());
                }

                return summary;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(bookingSummaries);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch all bookings: " + e.getMessage()));
        }
    }

    @GetMapping("/driver")
    public ResponseEntity<?> getDriverBookings() {
        try {
            // Get driver from authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Driver not authenticated"));
            }
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Driver driver = driverRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            // Get all bookings for the driver
            List<Booking> bookings = bookingRepository.findByDriver(driver);

            return ResponseEntity.ok(bookings.stream().map(booking -> {
                Map<String, Object> summary = new HashMap<>();
                summary.put("bookingId", booking.getBookingId());
                summary.put("pickupLocation", booking.getPickupLocation());
                summary.put("dropoffLocation", booking.getDropoffLocation());
                summary.put("status", booking.getStatus());
                summary.put("createdAt", booking.getCreatedAt().toString());
                summary.put("fare", booking.getFare());
                summary.put("pickupDateTime", booking.getPickupDateTime());
                if (booking.getCustomer() != null) {
                    summary.put("customerName", booking.getCustomer().getFullName());
                }

                if (booking.getVehicle() != null) {
                    summary.put("vehicleModel", booking.getVehicle().getModel());
                }

                return summary;
            }).collect(Collectors.toList()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch driver bookings: " + e.getMessage()));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingBookings() {
        try {
            // Get driver from authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Driver not authenticated"));
            }
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Driver driver = driverRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            // Get bookings with status pending and driver is assigned
            List<Booking> bookings = bookingRepository.findByDriverAndStatus(driver, BookingStatus.PENDING);

            return ResponseEntity.ok(bookings.stream().map(booking -> {
                Map<String, Object> summary = new HashMap<>();
                summary.put("bookingId", booking.getBookingId());
                summary.put("pickupLocation", booking.getPickupLocation());
                summary.put("dropoffLocation", booking.getDropoffLocation());
                summary.put("status", booking.getStatus());
                summary.put("createdAt", booking.getCreatedAt().toString());
                summary.put("fare", booking.getFare());
                summary.put("pickupDateTime", booking.getPickupDateTime());

                if (booking.getCustomer() != null) {
                    summary.put("customerName", booking.getCustomer().getFullName());
                }

                if (booking.getVehicle() != null) {
                    summary.put("vehicleModel", booking.getVehicle().getModel());
                }

                return summary;
            }).collect(Collectors.toList()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch pending bookings: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignDriverToBooking(
            @PathVariable Long id) {

        try {
            // get driver from authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Driver not authenticated"));
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Driver driver = driverRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            Optional<Booking> bookingOpt = bookingRepository.findById(id);
            if (!bookingOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Booking not found"));
            }

            Booking booking = bookingOpt.get();

            // Assign driver to booking
            booking.setStatus(Booking.BookingStatus.CONFIRMED);

            // Update driver status
            driver.setStatus(Driver.DriverStatus.BUSY);

            // Save changes
            driverRepository.save(driver);
            bookingRepository.save(booking);

            return ResponseEntity.ok(Map.of(
                    "message", "Booking assigned successfully",
                    "bookingId", booking.getBookingId(),
                    "driverId", driver.getDriverId(),
                    "status", booking.getStatus().toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to assign driver to booking: " + e.getMessage()));
        }
    }
}