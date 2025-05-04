package com.sdos.driveme.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.sdos.driveme.model.User;
import com.sdos.driveme.model.Vehicle;
import com.sdos.driveme.repository.BookingRepository;
import com.sdos.driveme.repository.UserRepository;
import com.sdos.driveme.repository.VehicleRepository;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@SuppressWarnings("removal")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private BookingRepository bookingRepository;
    
    @MockBean
    private VehicleRepository vehicleRepository;
    
    @Test
    public void testGetUserProfile_Success() throws Exception {
        // Arrange
        User user = new User();
        user.setUserId(1L);
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.fullName").value("Test User"))
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }
    
    @Test
    public void testGetUserVehicles_Success() throws Exception {
        // Arrange
        User user = new User();
        user.setUserId(1L);
        
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(1L);
        vehicle.setModel("Toyota Camry");
        vehicle.setCarNumber("ABC123");
        
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(vehicle);
        user.setVehicles(vehicles);
        
        when(vehicleRepository.findByUser(user)).thenReturn(vehicles);
        
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1/vehicles")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].vehicleId").value(1))
            .andExpect(jsonPath("$[0].model").value("Toyota Camry"))
            .andExpect(jsonPath("$[0].licensePlate").value("ABC123"));
    }
}