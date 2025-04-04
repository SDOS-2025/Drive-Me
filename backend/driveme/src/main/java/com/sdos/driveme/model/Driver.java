package com.sdos.driveme.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "drivers")
public class Driver {
    @Id
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

    public Driver(String name, String email, String phone, String aadhar_card, String license_number, String password_hash) {
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

    public void setDriver_id(Long driver_id) {
        this.driver_id = driver_id;
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

    public String getAadhar_card() {
        return aadhar_card;
    }

    public void setAadhar_card(String aadhar_card) {
        this.aadhar_card = aadhar_card;
    }

    public String getLicense_number() {
        return license_number;
    }

    public void setLicense_number(String license_number) {
        this.license_number = license_number;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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
}
