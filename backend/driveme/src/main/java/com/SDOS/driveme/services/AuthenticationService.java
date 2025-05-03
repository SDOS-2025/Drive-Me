package com.SDOS.driveme.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SDOS.driveme.DTO.LoginRequestDTO;
import com.SDOS.driveme.DTO.RegisterRequestDTO;
import com.SDOS.driveme.Exception.AuthenticationException;
import com.SDOS.driveme.Exception.UserException;
import com.SDOS.driveme.model.Driver;
import com.SDOS.driveme.model.User;
import com.SDOS.driveme.repository.DriverRepository;
import com.SDOS.driveme.repository.UserRepository;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

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
        try {
            // Check for existing email
            if (userRepository.findByEmail(input.getEmail()).isPresent()) {
                throw new UserException("Email is already registered");
            }
            
            // Check for existing phone
            if (userRepository.findByPhone(input.getPhone()).isPresent()) {
                throw new UserException("Phone number is already registered");
            }
            
            // Check for existing Aadhar
            if (userRepository.findByAadharCard(input.getAadharCard()).isPresent()) {
                throw new UserException("Aadhar card is already registered");
            }
            
            User user = new User()
                    .setFullName(input.getFullName())
                    .setEmail(input.getEmail())
                    .setPhone(input.getPhone())
                    .setAadharCard(input.getAadharCard())
                    .setPassword(passwordEncoder.encode(input.getPassword()));

            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            logger.error("Database error during user registration: {}", e.getMessage());
            
            // Extract specific database error message
            String errorMessage = e.getMessage().toLowerCase();
            
            if (errorMessage.contains("email")) {
                throw new UserException("Email is already registered");
            } else if (errorMessage.contains("phone")) {
                throw new UserException("Phone number is already registered");
            } else if (errorMessage.contains("aadhar")) {
                throw new UserException("Aadhar card is already registered");
            } else {
                throw new UserException("Registration failed due to data validation error");
            }
        } catch (UserException e) {
            // Rethrow UserExceptions
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during user registration", e);
            throw new UserException("Registration failed: " + e.getMessage());
        }
    }

    public Driver signupDriver(RegisterRequestDTO input) {
        try {
            // Check for existing email
            if (driverRepository.findByEmail(input.getEmail()).isPresent()) {
                throw new UserException("Email is already registered");
            }
            
            // Check for existing phone
            if (driverRepository.findByPhone(input.getPhone()).isPresent()) {
                throw new UserException("Phone number is already registered");
            }
            
            // Check for existing Aadhar
            if (driverRepository.findByAadharCard(input.getAadharCard()).isPresent()) {
                throw new UserException("Aadhar card is already registered");
            }
            
            // Check for existing license
            if (driverRepository.findByLicenseNumber(input.getlicenseNumber()).isPresent()) {
                throw new UserException("License number is already registered");
            }
            
            Driver driver = new Driver()
                    .setName(input.getFullName())
                    .setEmail(input.getEmail())
                    .setPhone(input.getPhone())
                    .setAadharCard(input.getAadharCard())
                    .setPasswordHash(passwordEncoder.encode(input.getPassword()))
                    .setLicenseNumber(input.getlicenseNumber());

            return driverRepository.save(driver);
        } catch (DataIntegrityViolationException e) {
            logger.error("Database error during driver registration: {}", e.getMessage());
            
            // Extract specific database error message
            String errorMessage = e.getMessage().toLowerCase();
            
            if (errorMessage.contains("email")) {
                throw new UserException("Email is already registered");
            } else if (errorMessage.contains("phone")) {
                throw new UserException("Phone number is already registered");
            } else if (errorMessage.contains("aadhar")) {
                throw new UserException("Aadhar card is already registered");
            } else if (errorMessage.contains("license")) {
                throw new UserException("License number is already registered");
            } else {
                throw new UserException("Registration failed due to data validation error");
            }
        } catch (UserException e) {
            // Rethrow UserExceptions
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during driver registration", e);
            throw new UserException("Registration failed: " + e.getMessage());
        }
    }

    public User authenticate(LoginRequestDTO input) {
        try {
            System.out.println(">>> Authenticating user with email or phone: " + input.getEmailOrPhone());
            
            if (input.getEmailOrPhone() == null || input.getPassword() == null) {
                throw new AuthenticationException("Email/Phone and password must not be null");
            }

            User user = null;
            if (userRepository.findByEmail(input.getEmailOrPhone()).isPresent()) {
                user = userRepository.findByEmail(input.getEmailOrPhone()).get();
            } else if (userRepository.findByPhone(input.getEmailOrPhone()).isPresent()) {
                user = userRepository.findByPhone(input.getEmailOrPhone()).get();
            } else {
                throw new AuthenticationException("User not found");
            }

            if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
                throw new AuthenticationException("Invalid password");
            }
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmailOrPhone(),
                            input.getPassword()
                    )
            );
            return user;
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Invalid credentials");
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                throw e;
            }
            logger.error("Authentication error", e);
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }

    public Driver authenticateDriver(LoginRequestDTO input) {
        try {
            Driver driver = null;

            if (driverRepository.findByEmail(input.getEmailOrPhone()).isPresent()) {
                driver = driverRepository.findByEmail(input.getEmailOrPhone()).get();
            } else if (driverRepository.findByPhone(input.getEmailOrPhone()).isPresent()) {
                driver = driverRepository.findByPhone(input.getEmailOrPhone()).get();
            } else {
                throw new AuthenticationException("Driver not found");
            }

            // Verify password manually instead of using authenticationManager
            if (!passwordEncoder.matches(input.getPassword(), driver.getPassword())) {
                throw new AuthenticationException("Invalid password");
            }

            // Authenticate using authenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmailOrPhone(),
                            input.getPassword()
                    )
            );

            return driver;
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Invalid credentials");
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                throw e;
            }
            logger.error("Driver authentication error", e);
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }

    public User authenticateAdmin(LoginRequestDTO input) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmailOrPhone(),
                            input.getPassword()
                    )
            );

            System.out.println(">>> Authenticating admin with email or phone: " + input.getEmailOrPhone());
            
            User user = null;
            
            if (userRepository.findByEmail(input.getEmailOrPhone()).isPresent()) {
                user = userRepository.findByEmail(input.getEmailOrPhone()).get();
            } else if (userRepository.findByPhone(input.getEmailOrPhone()).isPresent()) {
                user = userRepository.findByPhone(input.getEmailOrPhone()).get();
            } else {
                throw new AuthenticationException("Admin not found");
            }
            
            if (!user.isSuperuser()) {
                throw new AuthenticationException("Unauthorized: User is not an admin");
            }
            
            return user;
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Invalid credentials");
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                throw e;
            }
            logger.error("Admin authentication error", e);
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }
}