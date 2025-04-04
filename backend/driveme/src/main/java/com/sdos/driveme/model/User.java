package com.sdos.driveme.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;  // Renamed from user_id

    @Column(name = "full_name")
    private String fullName;

    private String email;

    private String phone;

    @Column(name = "aadhar_card")
    private String aadharCard;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "created_at")
    private String createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles;

    public User() {}

    public User(String fullName, String email, String phone, String aadharCard, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.aadharCard = aadharCard;
        this.passwordHash = passwordHash;
    }

    // 🔹 Getters and Setters
    public Long getId() {
        return user_id;
    }

    public void setId(Long id) {
        this.user_id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
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
}
