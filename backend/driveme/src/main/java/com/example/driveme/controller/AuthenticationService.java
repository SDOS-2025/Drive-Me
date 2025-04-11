package com.example.driveme.controller;



import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.driveme.DTO.LoginRequestDTO;
import com.example.driveme.DTO.RegisterRequestDTO;
import com.example.driveme.model.Driver;
import com.example.driveme.model.User;
import com.example.driveme.repository.DriverRepository;
import com.example.driveme.repository.UserRepository;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
        UserRepository userRepository,
        DriverRepository driverRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterRequestDTO input) {
        User user = new User()
                .setFullName(input.getFullName())
                .setEmail(input.getEmail())
                .setPhone(input.getPhone()) // MISSING: You need to set the phone
                .setAadharCard(input.getAadharCard()) // MISSING: You need to set the aadharCard
                .setPassword(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(user);
    }

    public User authenticate(LoginRequestDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmailOrPhone(),
                        input.getPassword()
                )
        );

        System.out.println(">>> Authenticating user with email or phone: " + input.getEmailOrPhone());
        
        if (userRepository.findByEmail(input.getEmailOrPhone()).isPresent()) {
            return userRepository.findByEmail(input.getEmailOrPhone()).get();
        } else if (userRepository.findByPhone(input.getEmailOrPhone()).isPresent()) {
            return userRepository.findByPhone(input.getEmailOrPhone()).get();
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public Driver authenticateDriver(LoginRequestDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmailOrPhone(),
                        input.getPassword()
                )
        );
        
        if (driverRepository.findByEmail(input.getEmailOrPhone()).isPresent()) {
            return driverRepository.findByEmail(input.getEmailOrPhone()).get();
        } else if (driverRepository.findByPhone(input.getEmailOrPhone()).isPresent()) {
            return driverRepository.findByPhone(input.getEmailOrPhone()).get();
        } else {
            throw new RuntimeException("Driver not found");
        }
    }

    public Driver signupDriver(RegisterRequestDTO input) {
        Driver driver = new Driver() // Replace 'ConcreteDriver' with the actual implementation class of Driver
                .setName(input.getFullName())
                .setEmail(input.getEmail())
                .setPhone(input.getPhone())
                .setAadhar_card(input.getAadharCard())
                .setPassword_hash(passwordEncoder.encode(input.getPassword()));

        return driverRepository.save(driver);
    }
}
