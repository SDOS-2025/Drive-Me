package com.example.driveme.DTO;

public class LoginResponseDTO {
    private String token;
    private Long userId;
    private String refreshToken;
    private String fullName;
    private Long expiresIn;
    private String licenseNumber;

    public LoginResponseDTO() {
    }

    // Constructor
    public LoginResponseDTO(String token, Long userId, String fullName) {
        this.token = token;
        this.userId = userId;
        this.fullName = fullName;
    }

    // Getters
    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    // Setters
    public LoginResponseDTO setToken(String token) {
        this.token = token;
        return this;
    }
    public LoginResponseDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }
    public LoginResponseDTO setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }
    public LoginResponseDTO setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
}
