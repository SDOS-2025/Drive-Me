package com.example.driveme.DTO;

public class VehicleResponseDTO {
    private Long id;
    private String model;
    private String registrationNumber;
    private String carNumber;
    private Long userId;
    
    public VehicleResponseDTO() {}

    public VehicleResponseDTO(Long id, String model, String registerationNumber, String carNumber, Long userId) {
        this.id = id;
        this.model = model;
        this.registrationNumber = registerationNumber;
        this.carNumber = carNumber;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public void setRegistrationNumber(String registerationNumber) {
        this.registrationNumber = registerationNumber;
    }
    public String getCarNumber() {
        return carNumber;
    }
    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    @Override
    public String toString() {
        return "VehicleDTO{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", carNumber='" + carNumber + '\'' +
                ", userId=" + userId +
                '}';
    }
    
}