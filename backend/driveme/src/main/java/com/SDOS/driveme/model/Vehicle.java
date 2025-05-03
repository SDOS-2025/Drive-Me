package com.sdos.driveme.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Vehicle must be associated with a user")
    private User user;

    @NotBlank(message = "Model is required")
    @Size(min = 2, max = 100, message = "Model must be between 2 and 100 characters")
    @Column(nullable = false)
    private String model;
    
    @NotBlank(message = "Registration number is required")
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;
    
    @NotBlank(message = "Car number is required")
    @Column(name = "car_number", nullable = false, unique = true)
    private String carNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType = VehicleType.SEDAN;
    
    @Column(name = "manufacture_year")
    private Integer manufactureYear;
    
    @Column(name = "color")
    private String color;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "vehicle")
    private List<Booking> bookings = new ArrayList<>();

    public enum VehicleType {
        HATCHBACK, SEDAN, SUV, LUXURY, VAN
    }

    // Constructors
    public Vehicle() {
    }

    public Vehicle(User user, String model, String registrationNumber, String carNumber) {
        this.user = user;
        this.model = model;
        this.registrationNumber = registrationNumber;
        this.carNumber = carNumber;
    }

    // Getters and Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }
    
    public VehicleType getVehicleType() {
        return vehicleType;
    }
    
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public Integer getManufactureYear() {
        return manufactureYear;
    }
    
    public void setManufactureYear(Integer manufactureYear) {
        this.manufactureYear = manufactureYear;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<Booking> getBookings() {
        return bookings;
    }

    // Pre-update callback
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId=" + vehicleId +
                ", model='" + model + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", carNumber='" + carNumber + '\'' +
                ", vehicleType=" + vehicleType +
                '}';
    }
}