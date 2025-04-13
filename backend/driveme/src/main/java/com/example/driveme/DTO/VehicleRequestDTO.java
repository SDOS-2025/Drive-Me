package com.example.driveme.DTO;

public class VehicleRequestDTO {
    private String model;
    private String registration_number;
    private String car_number;
    private Long user_id;

    public VehicleRequestDTO() {
    }

    public VehicleRequestDTO(String model, String registration_number, String car_number, Long user_id) {
        this.model = model;
        this.registration_number = registration_number;
        this.car_number = car_number;
        this.user_id = user_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
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
}
