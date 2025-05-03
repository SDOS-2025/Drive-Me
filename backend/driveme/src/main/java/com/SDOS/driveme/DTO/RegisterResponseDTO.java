package com.sdos.driveme.DTO;

public class RegisterResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String aadharCard;
    private String licenseNumber;

    public RegisterResponseDTO() {
    }

    public RegisterResponseDTO(Long id, String fullName, String email, String phone, String aadharCard, String licenseNumber) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.aadharCard = aadharCard;
        this.licenseNumber = licenseNumber;
    }

    public String getlicenseNumber() {
        return licenseNumber;
    }

    public RegisterResponseDTO setlicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
        return this;
    }

    public Long getId() {
        return id;
    }
    public RegisterResponseDTO setId(Long id) {
        this.id = id;
        return this;
    }
    public String getFullName() {
        return fullName;
    }
    public RegisterResponseDTO setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }
    public String getEmail() {
        return email;
    }
    public RegisterResponseDTO setEmail(String email) {
        this.email = email;
        return this;
    }
    public String getPhone() {
        return phone;
    }
    public RegisterResponseDTO setPhone(String phone) {
        this.phone = phone;
        return this;
    }
    public String getAadharCard() {
        return aadharCard;
    }
    public RegisterResponseDTO setAadharCard(String aadharCard) {
        this.aadharCard = aadharCard;
        return this;
    }
}
