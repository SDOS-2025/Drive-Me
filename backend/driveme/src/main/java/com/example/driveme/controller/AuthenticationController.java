package com.example.driveme.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.driveme.model.*;
import com.example.driveme.services.AuthenticationService;
import com.example.driveme.DTO.*;
import com.example.driveme.JwtAuth.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtServices jwtService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    
    private final AuthenticationService authenticationService;
    private final UserDetailsService userDetailsService;

    public AuthenticationController(JwtServices jwtService, AuthenticationService authenticationService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/user/signup")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO registerUserDto) {
        logger.info("Registering user: " + registerUserDto.getFullName() + " with email: " + registerUserDto.getEmail());
        User registeredUser = authenticationService.signup(registerUserDto);
    
        
        RegisterResponseDTO registerResponse = new RegisterResponseDTO()
                .setId(registeredUser.getUserId())
                .setFullName(registeredUser.getFullName())
                .setEmail(registeredUser.getEmail())
                .setPhone(registeredUser.getPhone())
                .setAadharCard(registeredUser.getAadharCard());
        logger.info("User registered successfully: " + registerResponse.getFullName() + " with email: " + registerResponse.getEmail());
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/driver/signup")
    public ResponseEntity<RegisterResponseDTO> registerDriver(@RequestBody RegisterRequestDTO registerUserDto) {
        Driver registeredDriver = authenticationService.signupDriver(registerUserDto);

        RegisterResponseDTO registerResponse = new RegisterResponseDTO()
                .setId(registeredDriver.getDriverId())
                .setFullName(registeredDriver.getName())
                .setEmail(registeredDriver.getEmail())
                .setPhone(registeredDriver.getPhone())
                .setAadharCard(registeredDriver.getAadharCard())
                .setlicenseNumber(registeredDriver.getLicenseNumber());


        return ResponseEntity.ok(registerResponse);
    }

    // Add this new refresh token endpoint
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        
        try {
            // Extract username from refresh token
            String username = jwtService.extractUsername(refreshToken);
            
            // Check if username exists and token is valid
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                // Generate new access token
                String newToken = jwtService.generateToken(userDetails);
                
                return ResponseEntity.ok(new TokenRefreshResponse(
                        newToken,
                        refreshToken,
                        jwtService.getExpirationTime()
                ));
            }
            
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error refreshing token", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@RequestBody LoginRequestDTO loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setToken(jwtToken);
        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setUserId(authenticatedUser.getUserId());
        loginResponse.setFullName(authenticatedUser.getFullName());
        logger.info("User authenticated successfully: " + loginResponse.getFullName() + " with email: " + authenticatedUser.getEmail());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/driver/login")
    public ResponseEntity<LoginResponseDTO> authenticateDriver(@RequestBody LoginRequestDTO loginUserDto) {
        Driver authenticatedDriver = authenticationService.authenticateDriver(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedDriver);
        String refreshToken = jwtService.generateRefreshToken(authenticatedDriver);

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setToken(jwtToken);
        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setUserId(authenticatedDriver.getDriverId());
        loginResponse.setFullName(authenticatedDriver.getName());
        loginResponse.setLicenseNumber(authenticatedDriver.getLicenseNumber());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponseDTO> authenticateAdmin(@RequestBody LoginRequestDTO loginUserDto) {
        User authenticatedAdmin = authenticationService.authenticateAdmin(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedAdmin);
        String refreshToken = jwtService.generateRefreshToken(authenticatedAdmin);

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setToken(jwtToken);
        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setUserId(authenticatedAdmin.getUserId());
        loginResponse.setFullName(authenticatedAdmin.getFullName());
        logger.info("Admin authenticated successfully: " + loginResponse.getFullName() + " with email: " + authenticatedAdmin.getEmail());

        return ResponseEntity.ok(loginResponse);
    }
}