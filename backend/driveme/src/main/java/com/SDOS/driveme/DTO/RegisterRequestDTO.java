package com.sdos.driveme.DTO;

public class RegisterRequestDTO {
   private String fullName;
    private String email;
    private String phone;
    private String aadharCard;
    private String licenseNumber;
    private String password;
    private boolean isSuperuser;

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }
    
    public RegisterRequestDTO setSuperuser(boolean isSuperuser) {
        this.isSuperuser = isSuperuser;
        return this;
    }

    public boolean isSuperuser() {
        return isSuperuser;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getAadharCard() {
        return aadharCard;
    }

    public void setAadharCard(String aadharCard) {
        this.aadharCard = aadharCard;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    } 
    public String getlicenseNumber() {
        return licenseNumber;
    }
    public void setlicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
}
