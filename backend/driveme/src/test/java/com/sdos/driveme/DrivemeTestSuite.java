package com.sdos.driveme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import com.sdos.driveme.DTO.LoginRequestDTO;
import com.sdos.driveme.DTO.LoginResponseDTO;
import com.sdos.driveme.DTO.RegisterRequestDTO;
import com.sdos.driveme.DTO.RegisterResponseDTO;
import com.sdos.driveme.DTO.VehicleRequestDTO;
import com.sdos.driveme.DTO.VehicleResponseDTO;
import com.sdos.driveme.JwtAuth.JwtServices;
import com.sdos.driveme.controller.AuthenticationController;
import com.sdos.driveme.controller.BookingController;
import com.sdos.driveme.controller.DriverController;
import com.sdos.driveme.controller.UserController;
import com.sdos.driveme.model.Booking;
import com.sdos.driveme.model.Driver;
import com.sdos.driveme.model.Driver.DriverStatus;
import com.sdos.driveme.model.User;
import com.sdos.driveme.model.Vehicle;
import com.sdos.driveme.repository.BookingRepository;
import com.sdos.driveme.repository.DriverRepository;
import com.sdos.driveme.repository.UserRepository;
import com.sdos.driveme.repository.VehicleRepository;
import com.sdos.driveme.services.AuthenticationService;

@SpringBootTest
@ActiveProfiles("test")
@SuppressWarnings({"null", "removal"})
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class DrivemeTestSuite {

    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private VehicleRepository vehicleRepository;
    
    @MockBean
    private BookingRepository bookingRepository;
    
    @MockBean
    private DriverRepository driverRepository;
    
    @MockBean
    private AuthenticationService authenticationService;
    
    @MockBean
    private JwtServices jwtService;
    
    @MockBean
    private Authentication authentication;
    
    @MockBean
    private UserDetails userDetails;
    
    // Controllers under test
    private UserController userController;
    @SuppressWarnings("unused")
    private BookingController bookingController;
    private DriverController driverController;
    private AuthenticationController authController;

    @BeforeEach
    void setup() {
        // Initialize controllers with mocked repositories
        userController = new UserController(userRepository, vehicleRepository);
        bookingController = new BookingController();
        driverController = new DriverController(driverRepository, bookingRepository);
        authController = new AuthenticationController(jwtService, authenticationService, null);
    }

    // =================== BASIC CONTEXT TEST ===================
    @Test
    @DisplayName("01. Context Loads")
    void contextLoads() {
        // Basic test to verify the context loads
        assertNotNull(userRepository);
        assertNotNull(vehicleRepository);
        assertNotNull(bookingRepository);
        assertNotNull(driverRepository);
    }

    // =================== REPOSITORY TESTS ===================
    @Nested
    @DisplayName("Repository Tests")
    class RepositoryTests {
        
        @Test
        @DisplayName("01. User Repository - Find By ID Not Found")
        void testUserRepositoryFindByIdNotFound() {
            // Arrange
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
            
            // Act
            Optional<User> result = userRepository.findById(1L);
            
            // Assert
            assertTrue(result.isEmpty());
        }
        
        @Test
        @DisplayName("02. User Repository - Find By ID Found")
        void testUserRepositoryFindByIdFound() {
            // Arrange
            User user = new User();
            user.setUserId(1L);
            user.setFullName("Test User");
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            
            // Act
            Optional<User> result = userRepository.findById(1L);
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals("Test User", result.get().getFullName());
        }
        
        @Test
        @DisplayName("03. Vehicle Repository - Find By User")
        void testVehicleRepositoryFindByUser() {
            // Arrange
            User user = new User();
            user.setUserId(1L);
            
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleId(1L);
            vehicle.setModel("Toyota Camry");
            vehicle.setCarNumber("ABC123");
            
            List<Vehicle> vehicles = List.of(vehicle);
            
            when(vehicleRepository.findByUser(any(User.class))).thenReturn(vehicles);
            
            // Act
            List<Vehicle> result = vehicleRepository.findByUser(user);
            
            // Assert
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals("Toyota Camry", result.get(0).getModel());
        }
        
        @Test
        @DisplayName("04. Booking Repository - Find By Customer")
        void testBookingRepositoryFindByCustomer() {
            // Arrange
            User customer = new User();
            customer.setUserId(1L);
            
            Booking booking = new Booking();
            booking.setBookingId(1L);
            booking.setPickupLocation("123 Main St");
            booking.setCustomer(customer);
            
            List<Booking> bookings = List.of(booking);
            
            when(bookingRepository.findByCustomer(any(User.class))).thenReturn(bookings);
            
            // Act
            List<Booking> result = bookingRepository.findByCustomer(customer);
            
            // Assert
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals("123 Main St", result.get(0).getPickupLocation());
        }
        
        @Test
        @DisplayName("05. Driver Repository - Find By Status")
        void testDriverRepositoryFindByStatus() {
            // Arrange
            Driver driver = new Driver();
            driver.setDriverId(1L);
            driver.setName("John Driver");
            driver.setStatus(DriverStatus.AVAILABLE);
            
            List<Driver> drivers = List.of(driver);
            
            when(driverRepository.findByStatus(DriverStatus.AVAILABLE)).thenReturn(drivers);
            
            // Act
            List<Driver> result = driverRepository.findByStatus(DriverStatus.AVAILABLE);
            
            // Assert
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals("John Driver", result.get(0).getName());
        }
    }
    
    // =================== USER CONTROLLER TESTS ===================
    @Nested
    @DisplayName("User Controller Tests")
    class UserControllerTests {
        
        @Test
        @DisplayName("01. Get User Profile - Success")
        void testGetUserProfile_Success() {
            // Arrange
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("test@example.com");
            
            User user = new User();
            user.setUserId(1L);
            user.setFullName("Test User");
            user.setEmail("test@example.com");
            
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            
            // Act
            ResponseEntity<?> response = userController.getUserProfile(authentication);
            
            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(user, response.getBody());
        }
        
        @Test
        @DisplayName("02. Get User Vehicles - Success")
        void testGetUserVehicles_Success() {
            // Arrange
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("test@example.com");
            
            User user = new User();
            user.setUserId(1L);
            user.setEmail("test@example.com");
            
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleId(1L);
            vehicle.setModel("Toyota Camry");
            vehicle.setCarNumber("ABC123");
            vehicle.setUser(user);
            
            List<Vehicle> vehicles = new ArrayList<>();
            vehicles.add(vehicle);
            user.setVehicles(vehicles);
            
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            
            // Act
            ResponseEntity<?> response = userController.getUserVehicles(authentication);
            
            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            @SuppressWarnings("unchecked")
            List<VehicleResponseDTO> result = (List<VehicleResponseDTO>) response.getBody();
            assertEquals(1, result.size());
            assertEquals("Toyota Camry", result.get(0).getModel());
        }
        
        @Test
        @DisplayName("03. Add Vehicle - Success")
        void testAddVehicle_Success() {
            // Arrange
            VehicleRequestDTO request = new VehicleRequestDTO();
            request.setUserId(1L);
            request.setModel("Honda Civic");
            request.setRegistrationNumber("REG123");
            request.setCarNumber("XYZ789");
            request.setVehicleType("SEDAN");
            
            User user = new User();
            user.setUserId(1L);
            user.setFullName("Test User");
            user.setVehicles(new ArrayList<>());
            
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);
            
            // Act
            ResponseEntity<?> response = userController.addVehicle(request);
            
            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            @SuppressWarnings("unchecked")
            Map<String, String> responseBody = (Map<String, String>) response.getBody();
            assertEquals("Vehicle added successfully", responseBody.get("message"));
            
            // Verify the vehicle was added to user's vehicles
            verify(userRepository).save(any(User.class));
        }
        
        @Test
        @DisplayName("04. Remove Vehicle - Success")
        void testRemoveVehicle_Success() {
            // Arrange
            Long vehicleId = 1L;
            
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("test@example.com");
            
            User user = new User();
            user.setUserId(1L);
            user.setEmail("test@example.com");
            
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleId(vehicleId);
            vehicle.setUser(user);
            
            List<Vehicle> vehicles = new ArrayList<>();
            vehicles.add(vehicle);
            user.setVehicles(vehicles);
            
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
            
            // Act
            ResponseEntity<?> response = userController.removeVehicle(vehicleId, authentication);
            
            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            @SuppressWarnings("unchecked")
            Map<String, String> responseBody = (Map<String, String>) response.getBody();
            assertEquals("Vehicle removed successfully", responseBody.get("message"));
            
            // Verify delete was called
            verify(vehicleRepository).delete(any(Vehicle.class));
        }
    }
    
    // =================== DRIVER CONTROLLER TESTS ===================
    @Nested
    @DisplayName("Driver Controller Tests")
    class DriverControllerTests {
        
        @Test
        @DisplayName("01. Get All Drivers - Success")
        void testGetAllDrivers_Success() {
            // Arrange
            Driver driver1 = new Driver();
            driver1.setDriverId(1L);
            driver1.setName("John Driver");
            driver1.setEmail("john@example.com");
            
            Driver driver2 = new Driver();
            driver2.setDriverId(2L);
            driver2.setName("Jane Driver");
            driver2.setEmail("jane@example.com");
            
            List<Driver> drivers = List.of(driver1, driver2);
            
            when(driverRepository.findAll()).thenReturn(drivers);
            
            // Act
            List<RegisterResponseDTO> response = driverController.getAllDrivers();
            
            // Assert
            assertEquals(2, response.size());
            assertEquals("John Driver", response.get(0).getFullName());
            assertEquals("Jane Driver", response.get(1).getFullName());
        }
        
        @Test
        @DisplayName("02. Get Driver Status - Success")
        void testGetDriverStatus_Success() {
            // Arrange
            Driver driver = new Driver();
            driver.setDriverId(1L);
            driver.setName("John Driver");
            driver.setPhone("1234567890");
            driver.setStatus(DriverStatus.AVAILABLE);
            driver.setAverageRating(4.5);
            
            List<Driver> drivers = List.of(driver);
            
            when(driverRepository.findAll()).thenReturn(drivers);
            when(bookingRepository.findByDriverAndStatusIn(eq(driver), anyList())).thenReturn(List.of());
            
            // Act
            ResponseEntity<List<Map<String, Object>>> response = driverController.getDriverStatus();
            
            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<Map<String, Object>> result = response.getBody();
            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).get("driverId"));
            assertEquals("John Driver", result.get(0).get("name"));
            assertEquals(DriverStatus.AVAILABLE, result.get(0).get("status"));
            assertEquals(4.5, result.get(0).get("averageRating"));
        }
        
        @Test
        @DisplayName("03. Update Driver Status - Success")
        void testUpdateDriverStatus_Success() {
            // Arrange
            Long driverId = 1L;
            Map<String, String> request = Map.of("status", "BUSY");
            
            Driver driver = new Driver();
            driver.setDriverId(driverId);
            driver.setName("John Driver");
            driver.setStatus(DriverStatus.AVAILABLE);
            
            when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
            when(driverRepository.save(any(Driver.class))).thenReturn(driver);
            
            // Act
            ResponseEntity<?> response = driverController.updateDriverStatus(driverId, request);
            
            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertEquals("Driver status updated successfully", responseBody.get("message"));
            assertEquals(driverId, responseBody.get("driverId"));
            assertEquals("BUSY", responseBody.get("status"));
            
            // Verify driver status was updated
            assertEquals(DriverStatus.BUSY, driver.getStatus());
        }
        
        @Test
        @DisplayName("04. Get Available Drivers - Success")
        void testGetAvailableDrivers_Success() {
            // Arrange
            Driver driver1 = new Driver();
            driver1.setDriverId(1L);
            driver1.setName("John Driver");
            driver1.setPhone("1234567890");
            driver1.setAverageRating(4.5);
            
            Driver driver2 = new Driver();
            driver2.setDriverId(2L);
            driver2.setName("Jane Driver");
            driver2.setPhone("0987654321");
            driver2.setAverageRating(4.8);
            
            List<Driver> availableDrivers = List.of(driver1, driver2);
            
            when(driverRepository.findByStatus(DriverStatus.AVAILABLE)).thenReturn(availableDrivers);
            
            // Act
            ResponseEntity<?> response = driverController.getAvailableDrivers();
            
            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> result = (List<Map<String, Object>>) response.getBody();
            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).get("driverId"));
            assertEquals("John Driver", result.get(0).get("name"));
            assertEquals(4.5, result.get(0).get("rating"));
        }
    }
    
    // =================== AUTH CONTROLLER TESTS ===================
    @Nested
    @DisplayName("Authentication Controller Tests")
    class AuthControllerTests {
        
        @Test
        @DisplayName("01. User Registration - Success")
        void testUserRegistration_Success() {
            // Arrange
            RegisterRequestDTO request = new RegisterRequestDTO();
            request.setFullName("Test User");
            request.setEmail("test@example.com");
            request.setPhone("1234567890");
            request.setPassword("Password123");
            
            User user = new User();
            user.setUserId(1L);
            user.setFullName("Test User");
            user.setEmail("test@example.com");
            user.setPhone("1234567890");
            
            when(authenticationService.signup(any(RegisterRequestDTO.class))).thenReturn(user);
            
            // Act
            ResponseEntity<?> response = authController.register(request);
            
            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            RegisterResponseDTO result = (RegisterResponseDTO) response.getBody();
            assertEquals(1L, result.getId());
            assertEquals("Test User", result.getFullName());
            assertEquals("test@example.com", result.getEmail());
        }
        
        @Test
        @DisplayName("02. Driver Registration - Success")
        void testDriverRegistration_Success() {
            // Arrange
            RegisterRequestDTO request = new RegisterRequestDTO();
            request.setFullName("Test Driver");
            request.setEmail("driver@example.com");
            request.setPhone("1234567890");
            request.setPassword("Password123");
            request.setlicenseNumber("LIC123456");
            
            Driver driver = new Driver();
            driver.setDriverId(1L);
            driver.setName("Test Driver");
            driver.setEmail("driver@example.com");
            driver.setPhone("1234567890");
            driver.setLicenseNumber("LIC123456");
            
            when(authenticationService.signupDriver(any(RegisterRequestDTO.class))).thenReturn(driver);
            
            // Act
            ResponseEntity<?> response = authController.registerDriver(request);
            
            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            RegisterResponseDTO result = (RegisterResponseDTO) response.getBody();
            assertEquals(1L, result.getId());
            assertEquals("Test Driver", result.getFullName());
            assertEquals("driver@example.com", result.getEmail());
            assertEquals("LIC123456", result.getlicenseNumber());
        }
        
        @Test
        @DisplayName("03. User Login - Success")
        void testUserLogin_Success() {
            // Arrange
            LoginRequestDTO request = new LoginRequestDTO();
            request.setEmailOrPhone("test@example.com");
            request.setPassword("Password123");
            
            User user = new User();
            user.setUserId(1L);
            user.setFullName("Test User");
            user.setEmail("test@example.com");
            
            when(authenticationService.authenticate(any(LoginRequestDTO.class))).thenReturn(user);
            when(jwtService.generateToken(any(User.class))).thenReturn("access-token");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");
            when(jwtService.getExpirationTime()).thenReturn(3600L);
            
            // Act
            ResponseEntity<?> response = authController.authenticate(request);
            
            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            LoginResponseDTO result = (LoginResponseDTO) response.getBody();
            assertEquals("access-token", result.getToken());
            assertEquals("refresh-token", result.getRefreshToken());
            assertEquals(3600L, result.getExpiresIn());
            assertEquals(1L, result.getUserId());
            assertEquals("Test User", result.getFullName());
        }
        
        @Test
        @DisplayName("04. Driver Login - Success")
        void testDriverLogin_Success() {
            // Arrange
            LoginRequestDTO request = new LoginRequestDTO();
            request.setEmailOrPhone("driver@example.com");
            request.setPassword("Password123");
            
            Driver driver = new Driver();
            driver.setDriverId(1L);
            driver.setName("Test Driver");
            driver.setEmail("driver@example.com");
            driver.setLicenseNumber("LIC123456");
            
            when(authenticationService.authenticateDriver(any(LoginRequestDTO.class))).thenReturn(driver);
            when(jwtService.generateToken(any(Driver.class))).thenReturn("access-token");
            when(jwtService.generateRefreshToken(any(Driver.class))).thenReturn("refresh-token");
            when(jwtService.getExpirationTime()).thenReturn(3600L);
            
            // Act
            ResponseEntity<?> response = authController.authenticateDriver(request);
            
            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            LoginResponseDTO result = (LoginResponseDTO) response.getBody();
            assertEquals("access-token", result.getToken());
            assertEquals("refresh-token", result.getRefreshToken());
            assertEquals(3600L, result.getExpiresIn());
            assertEquals(1L, result.getUserId());
            assertEquals("Test Driver", result.getFullName());
            assertEquals("LIC123456", result.getLicenseNumber());
        }
    }
}