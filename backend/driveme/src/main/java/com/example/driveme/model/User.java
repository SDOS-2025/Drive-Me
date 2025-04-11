package com.example.driveme.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id; // Renamed from user_id

    @Column(name = "full_name")
    private String fullName;

    private String email;

    private String phone;

    @Column(name = "aadhar_card")
    private String aadharCard;

    @Column(name = "password_hash")
    private String password;

    @Column(name = "created_at")
    private String createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles;

    public User() {
    }

    public User(String fullName, String email, String phone, String aadharCard, String password) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.aadharCard = aadharCard;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    public String setUsername(String username) {
        this.fullName = username;
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return fullName;
    }

    // 🔹 Getters and Setters
    public Long getId() {
        return user_id;
    }

    public User setId(Long id) {
        this.user_id = id;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public User setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAadharCard() {
        return aadharCard;
    }

    public User setAadharCard(String aadharCard) {
        this.aadharCard = aadharCard;
        return this;
    }

    public String getPasswordHash() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public User setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
        for (Vehicle vehicle : vehicles) {
            vehicle.setUser(this);
        }
        return this;
    }

    // 🔹 toString
    @Override
    public String toString() {
        return "User{" +
                "id=" + user_id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", aadharCard='" + aadharCard + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    // Add these to both User and Driver classes
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
