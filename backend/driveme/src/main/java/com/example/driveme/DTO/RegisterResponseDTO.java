package com.example.driveme.DTO;

public class RegisterResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String aadharCard;

    public RegisterResponseDTO() {
    }

    public RegisterResponseDTO(Long id, String fullName, String email, String phone, String aadharCard) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.aadharCard = aadharCard;
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
