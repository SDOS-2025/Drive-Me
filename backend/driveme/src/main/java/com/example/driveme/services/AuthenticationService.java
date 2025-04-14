package com.example.driveme.services;



import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    try {
        // First, check if driver exists
        Driver driver = null;

        if (driverRepository.findByEmail(input.getEmailOrPhone()).isPresent()) {
            driver = driverRepository.findByEmail(input.getEmailOrPhone()).get();
        } else if (driverRepository.findByPhone(input.getEmailOrPhone()).isPresent()) {
            driver = driverRepository.findByPhone(input.getEmailOrPhone()).get();
        } else {
            throw new RuntimeException("Driver not found");
        }

        // Verify password manually instead of using authenticationManager
        if (!passwordEncoder.matches(input.getPassword(), driver.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return driver;
    } catch (BadCredentialsException e) {
        throw new RuntimeException("Invalid credentials", e);
    } catch (Exception e) {
        throw new RuntimeException("Authentication failed", e);
    }
}

    public Driver signupDriver(RegisterRequestDTO input) {
        Driver driver = new Driver() // Replace 'ConcreteDriver' with the actual implementation class of Driver
                .setName(input.getFullName())
                .setEmail(input.getEmail())
                .setPhone(input.getPhone())
                .setAadharCard(input.getAadharCard())
                .setPasswordHash(passwordEncoder.encode(input.getPassword()))
                .setLicenseNumber(input.getlicenseNumber());

        return driverRepository.save(driver);
    }
}
