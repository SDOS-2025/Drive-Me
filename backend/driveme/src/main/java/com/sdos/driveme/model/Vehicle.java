package com.sdos.driveme.model;
import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicle_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String model;
    private String registration_number;
    private String car_number; // Moved from users table
    private String created_at;

    // Constructor
    public Vehicle() {
    }

    public Vehicle(User user, String model, String registration_number, String car_number, String created_at) {
        this.user = user;
        this.model = model;
        this.registration_number = registration_number;
        this.car_number = car_number;
        this.created_at = created_at;
    }

    // Getters and Setters
    public Long getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(Long vehicle_id) {
        this.vehicle_id = vehicle_id;
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

    public String getRegistration_number() {
        return registration_number;
    }

    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    // toString
    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicle_id=" + vehicle_id +
                ", user=" + user +
                ", model='" + model + '\'' +
                ", registration_number='" + registration_number + '\'' +
                ", car_number='" + car_number + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
