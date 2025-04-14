package com.example.driveme.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "drivers")
public class Driver implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driverId;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    @Column(unique = true, nullable = false)
    private String phone;

    @NotBlank(message = "Aadhar card is required")
    @Pattern(regexp = "^\\d{12}$", message = "Aadhar card must be 12 digits")
    @Column(name = "aadhar_card", unique = true, nullable = false)
    private String aadharCard;

    @NotBlank(message = "License number is required")
    @Column(name = "license_number", unique = true, nullable = false)
    private String licenseNumber;
    
    @Column(name = "license_expiry")
    private LocalDateTime licenseExpiry;

    @NotBlank(message = "Password is required")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DriverStatus status = DriverStatus.AVAILABLE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 20)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;
    
    @Column(name = "current_location")
    private String currentLocation;
    
    @Column(name = "average_rating")
    private Double averageRating;
    
    @Column(name = "total_trips")
    private Integer totalTrips = 0;
    
    @OneToMany(mappedBy = "driver")
    private List<Booking> bookings = new ArrayList<>();

    public enum DriverStatus {
        AVAILABLE, BUSY, OFFLINE
    }
    
    public enum AccountStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED
    }

    // Constructors
    public Driver() {
    }

    public Driver(String name, String email, String phone, String aadharCard, String licenseNumber,
            String passwordHash) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.aadharCard = aadharCard;
        this.licenseNumber = licenseNumber;
        this.passwordHash = passwordHash;
    }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_DRIVER"));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email; // Use email as username for authentication
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountStatus != AccountStatus.DELETED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountStatus != AccountStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    // Getters and Setters
    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAadharCard() {
        return aadharCard;
    }

    public void setAadharCard(String aadharCard) {
        this.aadharCard = aadharCard;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public LocalDateTime getLicenseExpiry() {
        return licenseExpiry;
    }
    
    public void setLicenseExpiry(LocalDateTime licenseExpiry) {
        this.licenseExpiry = licenseExpiry;
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

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }
    
    public AccountStatus getAccountStatus() {
        return accountStatus;
    }
    
    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
    
    public String getCurrentLocation() {
        return currentLocation;
    }
    
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
    
    public Double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    
    public Integer getTotalTrips() {
        return totalTrips;
    }
    
    public void setTotalTrips(Integer totalTrips) {
        this.totalTrips = totalTrips;
    }
    
    public void incrementTotalTrips() {
        this.totalTrips++;
    }
    
    public List<Booking> getBookings() {
        return bookings;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // Pre-update callback
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Driver{" +
                "driverId=" + driverId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", status=" + status +
                ", totalTrips=" + totalTrips +
                '}';
    }
}