package com.SDOS.driveme.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false, referencedColumnName = "userId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull(message = "Customer is required")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "driverId")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false, referencedColumnName = "vehicleId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull(message = "Vehicle is required")
    private Vehicle vehicle;

    @NotBlank(message = "Pickup location is required")
    @Column(name = "pickup_location", nullable = false)
    private String pickupLocation;

    @NotBlank(message = "Dropoff location is required")
    @Column(name = "dropoff_location", nullable = false)
    private String dropoffLocation;

    @Column(name = "pickup_date_time", nullable = false)
    private String pickupDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @DecimalMin(value = "0.0", inclusive = true, message = "Fare must be a positive number")
    @Column(name = "fare", precision = 10, scale = 2)
    private BigDecimal fare;

    @NotNull(message = "Esimated duration is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Estimated distance must be a positive number")
    @Column(name = "estimated_duration_km")
    private Integer estimatedDuration;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "completed_at")
    @PastOrPresent(message = "Completion time cannot be in the future")
    private LocalDateTime completedAt;
    
    @Column(name = "customer_rating")
    private Integer customerRating;
    
    @Column(name = "driver_rating")
    private Integer driverRating;
    
    @Column(name = "customer_feedback", length = 500)
    private String customerFeedback;
    
    @Column(name = "driver_feedback", length = 500)
    private String driverFeedback;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    public enum BookingStatus {
        CONFIRMED, PENDING, CANCELLED, COMPLETED
    }

    // Constructors
    public Booking() {}
    
    public Booking(User customer, Vehicle vehicle, String pickupLocation, String dropoffLocation, 
            LocalDateTime pickupTime) {
        this.customer = customer;
        this.vehicle = vehicle;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
    }

    // Business logic methods
    public void confirmBooking(Driver driver, BigDecimal fare) {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("Booking must be in PENDING state to confirm");
        }
        this.driver = driver;
        this.fare = fare;
        this.status = BookingStatus.CONFIRMED;
    }
    
    public void completeTrip(Double actualDistanceKm) {
        if (this.status == BookingStatus.COMPLETED)
        {
            return;
        }
        if (this.status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Trip must be CONFIRMED to complete");
        }
        this.status = BookingStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        if (this.driver != null) {
            this.driver.incrementTotalTrips();
        }
    }
    
    public void cancelBooking(String reason) {
        if (this.status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed booking");
        }
        this.status = BookingStatus.CANCELLED;
    }
    
    public void rateDriver(int rating, String feedback) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.driverRating = rating;
        this.driverFeedback = feedback;
    }
    
    public void rateCustomer(int rating, String feedback) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.customerRating = rating;
        this.customerFeedback = feedback;
    }

    // Standard getters and setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public User getCustomer() {
        return customer;
    }

    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public void setPickUpDateTime(String pickupDateTime) {
        this.pickupDateTime = pickupDateTime;
    }

    public String getPickupDateTime() {
        return pickupDateTime;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }
    
    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public void setFare(BigDecimal fare) {
        this.fare = fare;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public Integer getCustomerRating() {
        return customerRating;
    }
    
    public void setCustomerRating(Integer customerRating) {
        this.customerRating = customerRating;
    }
    
    public Integer getDriverRating() {
        return driverRating;
    }
    
    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }
    
    public String getCustomerFeedback() {
        return customerFeedback;
    }
    
    public void setCustomerFeedback(String customerFeedback) {
        this.customerFeedback = customerFeedback;
    }
    
    public String getDriverFeedback() {
        return driverFeedback;
    }
    
    public void setDriverFeedback(String driverFeedback) {
        this.driverFeedback = driverFeedback;
    }
    
    public List<Payment> getPayments() {
        return payments;
    }
    
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
    
    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setBooking(this);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", customer=" + (customer != null ? customer.getUserId() : null) +
                ", driver=" + (driver != null ? driver.getDriverId() : null) +
                ", vehicle=" + (vehicle != null ? vehicle.getVehicleId() : null) +
                ", pickupLocation='" + pickupLocation + '\'' +
                ", dropoffLocation='" + dropoffLocation + '\'' +
                ", status=" + status +
                ", fare=" + fare +
                ", createdAt=" + createdAt +
                '}';
    }
}