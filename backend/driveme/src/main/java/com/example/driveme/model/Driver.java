package com.example.driveme.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "drivers")
public class Driver implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driver_id;
    private String name;
    private String email;
    private String phone;
    private String aadhar_card;
    private String license_number;
    private String password_hash;
    private String created_at;

    public Driver() {
    }

    public Driver(String name, String email, String phone, String aadhar_card, String license_number,
            String password_hash) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.aadhar_card = aadhar_card;
        this.license_number = license_number;
        this.password_hash = password_hash;
    }

    public Long getDriver_id() {
        return driver_id;
    }

    public Driver setDriver_id(Long driver_id) {
        this.driver_id = driver_id;
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

    public String getAadhar_card() {
        return aadhar_card;
    }

    public Driver setAadhar_card(String aadhar_card) {
        this.aadhar_card = aadhar_card;
        return this;
    }

    public String getLicense_number() {
        return license_number;
    }

    public Driver setLicense_number(String license_number) {
        this.license_number = license_number;
        return this;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public Driver setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
        return this;
    }

    public String getCreated_at() {
        return created_at;
    }

    public Driver setCreated_at(String created_at) {
        this.created_at = created_at;
        return this;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "driver_id=" + driver_id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", aadhar_card='" + aadhar_card + '\'' +
                ", license_number='" + license_number + '\'' +
                ", password_hash='" + password_hash + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }

    @Override
    public String getPassword() {
        return password_hash;
    }

    @Override
    public String getUsername() {
        return name;
    }

    // Add these to both User and Driver classes
    // Assuming Driver implements UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_DRIVER"));
    }

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
