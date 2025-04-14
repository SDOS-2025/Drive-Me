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

    public Driver setDriverId(Long driverId) {
        this.driverId = driverId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Driver setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Driver setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Driver setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAadharCard() {
        return aadharCard;
    }

    public Driver setAadharCard(String aadharCard) {
        this.aadharCard = aadharCard;
        return this;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public Driver setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Driver setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public Driver setStatus(DriverStatus status) {
        this.status = status;
        return this;
    }
    
    public AccountStatus getAccountStatus() {
        return accountStatus;
    }
    
    public Driver setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
        return this;
    }
    
    public Double getAverageRating() {
        return averageRating;
    }
    
    public Driver setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
        return this;
    }
    
    public Integer getTotalTrips() {
        return totalTrips;
    }
    
    public Driver setTotalTrips(Integer totalTrips) {
        this.totalTrips = totalTrips;
        return this;
    }
    
    public Driver incrementTotalTrips() {
        this.totalTrips++;
        return this;
    }
    
    public List<Booking> getBookings() {
        return bookings;
    }

    public Driver setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
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