package com.sdos.driveme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdos.driveme.model.Booking;
import com.sdos.driveme.model.Driver;
import com.sdos.driveme.model.User;
import com.sdos.driveme.model.Booking.BookingStatus;
import com.sdos.driveme.repository.BookingRepository;
import com.sdos.driveme.repository.DriverRepository;
import com.sdos.driveme.repository.UserRepository;

@WebMvcTest(BookingController.class)
@ActiveProfiles("test")
@SuppressWarnings("removal")
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private BookingRepository bookingRepository;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private DriverRepository driverRepository;
    
    @Test
    public void testCreateBooking_Success() throws Exception {
        // Arrange
        User user = new User();
        user.setUserId(1L);
        
        Driver driver = new Driver();
        driver.setDriverId(1L);
        
        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setCustomer(user);
        booking.setDriver(driver);
        booking.setPickupLocation("123 Main St");
        booking.setDropoffLocation("456 Elm St");
        BookingStatus status = BookingStatus.PENDING;
        booking.setStatus(status);
        
        Map<String, String> request = new HashMap<>();
        request.put("userId", "1");
        request.put("driverId", "1");
        request.put("pickupLocation", "123 Main St");
        request.put("dropoffLocation", "456 Elm St");
        request.put("pickupDateTime", LocalDateTime.now().toString());
        
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(driverRepository.findById(anyLong())).thenReturn(Optional.of(driver));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bookings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Booking created successfully"));
    }
    
    @Test
    public void testCancelBooking_Success() throws Exception {
        // Arrange
        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setStatus(BookingStatus.CANCELLED);
        
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/bookings/cancel/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Booking cancelled successfully"));
    }
}