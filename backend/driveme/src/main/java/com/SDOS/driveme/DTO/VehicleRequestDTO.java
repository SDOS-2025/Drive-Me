package com.SDOS.driveme.DTO;

public class VehicleRequestDTO {
    private String model;
    private String registrationNumber;
    private String carNumber;
    private String vehicleType;
    private Long userId;

    public VehicleRequestDTO() {
    }

    public VehicleRequestDTO(String model, String registration_number, String car_number, Long user_id, String vehicle_type) {
        this.model = model;
        this.registrationNumber = registration_number;
        this.carNumber = car_number;
        this.userId = user_id;
        this.vehicleType = vehicle_type;
    }

    public String getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(String vehicle_type) {
        this.vehicleType = vehicle_type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long user_id) {
        this.userId = user_id;
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

    public void setRegistrationNumber(String registration_number) {
        this.registrationNumber = registration_number;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String car_number) {
        this.carNumber = car_number;
    }
}
