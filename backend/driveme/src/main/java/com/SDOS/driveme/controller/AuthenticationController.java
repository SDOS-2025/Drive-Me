package com.SDOS.driveme.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SDOS.driveme.DTO.ErrorResponseDTO;
import com.SDOS.driveme.DTO.LoginRequestDTO;
import com.SDOS.driveme.DTO.LoginResponseDTO;
import com.SDOS.driveme.DTO.RegisterRequestDTO;
import com.SDOS.driveme.DTO.RegisterResponseDTO;
import com.SDOS.driveme.DTO.TokenRefreshRequest;
import com.SDOS.driveme.DTO.TokenRefreshResponse;
import com.SDOS.driveme.Exception.AuthenticationException;
import com.SDOS.driveme.Exception.InvalidTokenException;
import com.SDOS.driveme.Exception.UserException;
import com.SDOS.driveme.JwtAuth.JwtServices;
import com.SDOS.driveme.model.Driver;
import com.SDOS.driveme.model.User;
import com.SDOS.driveme.services.AuthenticationService;

import jakarta.validation.Valid;

@RequestMapping("/auth")
@RestController
@Validated
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
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerUserDto) {
        logger.info("Attempting to register user: {}", registerUserDto.getFullName());
        
        try {
            // Validate the request DTO
            if (registerUserDto.getEmail() == null || registerUserDto.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponseDTO("Email is required", "Validation error", HttpStatus.BAD_REQUEST.value()));
            }
            
            if (registerUserDto.getPassword() == null || registerUserDto.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponseDTO("Password is required", "Validation error", HttpStatus.BAD_REQUEST.value()));
            }
            
            // Try to register the user
            User registeredUser = authenticationService.signup(registerUserDto);
            
            RegisterResponseDTO registerResponse = new RegisterResponseDTO()
                    .setId(registeredUser.getUserId())
                    .setFullName(registeredUser.getFullName())
                    .setEmail(registeredUser.getEmail())
                    .setPhone(registeredUser.getPhone())
                    .setAadharCard(registeredUser.getAadharCard());
            
            logger.info("User registered successfully: {} with email: {}", registerResponse.getFullName(), registerResponse.getEmail());
            return ResponseEntity.ok(registerResponse);
        } catch (UserException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(e.getMessage(), "Registration failed", HttpStatus.CONFLICT.value()));
        } catch (Exception e) {
            logger.error("Error during user registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("Registration failed due to an unexpected error", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/driver/signup")
    public ResponseEntity<?> registerDriver(@Valid @RequestBody RegisterRequestDTO registerUserDto) {
        logger.info("Attempting to register driver: {}", registerUserDto.getFullName());
        
        try {
            // Validate license number for drivers
            if (registerUserDto.getlicenseNumber() == null || registerUserDto.getlicenseNumber().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponseDTO("License number is required for drivers", "Validation error", HttpStatus.BAD_REQUEST.value()));
            }
            
            Driver registeredDriver = authenticationService.signupDriver(registerUserDto);

            RegisterResponseDTO registerResponse = new RegisterResponseDTO()
                    .setId(registeredDriver.getDriverId())
                    .setFullName(registeredDriver.getName())
                    .setEmail(registeredDriver.getEmail())
                    .setPhone(registeredDriver.getPhone())
                    .setAadharCard(registeredDriver.getAadharCard())
                    .setlicenseNumber(registeredDriver.getLicenseNumber());

            logger.info("Driver registered successfully: {} with email: {}", registerResponse.getFullName(), registerResponse.getEmail());
            return ResponseEntity.ok(registerResponse);
        } catch (UserException e) {
            logger.warn("Driver registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(e.getMessage(), "Registration failed", HttpStatus.CONFLICT.value()));
        } catch (Exception e) {
            logger.error("Error during driver registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("Registration failed due to an unexpected error", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        logger.info("Processing token refresh request");
        
        if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            logger.warn("Token refresh failed: No refresh token provided");
            return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO("Refresh token is required", "Validation error", HttpStatus.BAD_REQUEST.value()));
        }
        
        String refreshToken = request.getRefreshToken();
        
        try {
            // Extract username from refresh token
            String username = jwtService.extractUsername(refreshToken);
            
            if (username == null) {
                throw new InvalidTokenException("Invalid refresh token");
            }
            
            // Check if username exists and token is valid
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (userDetails == null) {
                throw new InvalidTokenException("User not found for token");
            }
            
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                // Generate new access token
                String newToken = jwtService.generateToken(userDetails);
                
                return ResponseEntity.ok(new TokenRefreshResponse(
                        newToken,
                        refreshToken,
                        jwtService.getExpirationTime()
                ));
            } else {
                throw new InvalidTokenException("Refresh token validation failed");
            }
        } catch (InvalidTokenException e) {
            logger.warn("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(e.getMessage(), "Token error", HttpStatus.UNAUTHORIZED.value()));
        } catch (Exception e) {
            logger.error("Error refreshing token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("Token refresh failed due to an unexpected error", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequestDTO loginUserDto) {
        logger.info("Processing user login request for: {}", loginUserDto.getEmailOrPhone());
        
        try {
            // Validate request
            if (loginUserDto.getEmailOrPhone() == null || loginUserDto.getEmailOrPhone().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO("Email or phone is required", "Validation error", HttpStatus.BAD_REQUEST.value()));
            }
            
            if (loginUserDto.getPassword() == null || loginUserDto.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO("Password is required", "Validation error", HttpStatus.BAD_REQUEST.value()));
            }
            
            User authenticatedUser = authenticationService.authenticate(loginUserDto);

            String jwtToken = jwtService.generateToken(authenticatedUser);
            String refreshToken = jwtService.generateRefreshToken(authenticatedUser);

            LoginResponseDTO loginResponse = new LoginResponseDTO();
            loginResponse.setToken(jwtToken);
            loginResponse.setRefreshToken(refreshToken);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());
            loginResponse.setUserId(authenticatedUser.getUserId());
            loginResponse.setFullName(authenticatedUser.getFullName());
            
            logger.info("User authenticated successfully: {} with email: {}", loginResponse.getFullName(), authenticatedUser.getEmail());
            return ResponseEntity.ok(loginResponse);
        } catch (AuthenticationException e) {
            logger.warn("User authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(e.getMessage(), "Authentication failed", HttpStatus.UNAUTHORIZED.value()));
        } catch (Exception e) {
            logger.error("Error during user authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("Authentication failed due to an unexpected error", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/driver/login")
    public ResponseEntity<?> authenticateDriver(@RequestBody LoginRequestDTO loginUserDto) {
        logger.info("Processing driver login request for: {}", loginUserDto.getEmailOrPhone());
        
        try {
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

            logger.info("Driver authenticated successfully: {}", loginResponse.getFullName());
            return ResponseEntity.ok(loginResponse);
        } catch (AuthenticationException e) {
            logger.warn("Driver authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(e.getMessage(), "Authentication failed", HttpStatus.UNAUTHORIZED.value()));
        } catch (Exception e) {
            logger.error("Error during driver authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("Authentication failed due to an unexpected error", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> authenticateAdmin(@RequestBody LoginRequestDTO loginUserDto) {
        logger.info("Processing admin login request for: {}", loginUserDto.getEmailOrPhone());
        
        try {
            User authenticatedAdmin = authenticationService.authenticateAdmin(loginUserDto);

            String jwtToken = jwtService.generateToken(authenticatedAdmin);
            String refreshToken = jwtService.generateRefreshToken(authenticatedAdmin);

            LoginResponseDTO loginResponse = new LoginResponseDTO();
            loginResponse.setToken(jwtToken);
            loginResponse.setRefreshToken(refreshToken);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());
            loginResponse.setUserId(authenticatedAdmin.getUserId());
            loginResponse.setFullName(authenticatedAdmin.getFullName());
            
            logger.info("Admin authenticated successfully: {} with email: {}", loginResponse.getFullName(), authenticatedAdmin.getEmail());
            return ResponseEntity.ok(loginResponse);
        } catch (AuthenticationException e) {
            logger.warn("Admin authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(e.getMessage(), "Authentication failed", HttpStatus.UNAUTHORIZED.value()));
        } catch (Exception e) {
            logger.error("Error during admin authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("Authentication failed due to an unexpected error", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}