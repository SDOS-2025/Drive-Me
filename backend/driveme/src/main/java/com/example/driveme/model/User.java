package com.example.driveme.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")  // Optional, but ensures correct table mapping
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Ensure correct ID generation strategy
    private Long user_id;
    @Column(name = "full_name") // Map to correct column name in DB
    private String fullName; 
    private String email;
    private String phone;
    private String car_number;
    private String rc_book;
    private String aadhar_card;
    private String password_hash;
    private String created_at;

    public User() {
    }

    public User(String full_name, String email, String phone, String car_number, String rc_book, String aadhar_card, String password_hash) {
        this.fullName = full_name;
        this.email = email;
        this.phone = phone;
        this.car_number = car_number;
        this.rc_book = rc_book;
        this.aadhar_card = aadhar_card;
        this.password_hash = password_hash;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getFull_name() {
        return fullName;
    }

    public void setFull_name(String full_name) {
        this.fullName = full_name;
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

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getRc_book() {
        return rc_book;
    }

    public void setRc_book(String rc_book) {
        this.rc_book = rc_book;
    }

    public String getAadhar_card() {
        return aadhar_card;
    }

    public void setAadhar_card(String aadhar_card) {
        this.aadhar_card = aadhar_card;
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

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", full_name='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", car_number='" + car_number + '\'' +
                ", rc_book='" + rc_book + '\'' +
                ", aadhar_card='" + aadhar_card + '\'' +
                ", password_hash='" + password_hash + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
