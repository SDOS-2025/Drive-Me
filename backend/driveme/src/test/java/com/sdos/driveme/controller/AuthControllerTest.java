package com.sdos.driveme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdos.driveme.DTO.LoginRequestDTO;
import com.sdos.driveme.DTO.RegisterRequestDTO;
import com.sdos.driveme.JwtAuth.JwtServices;
import com.sdos.driveme.model.User;
import com.sdos.driveme.services.AuthenticationService;

@WebMvcTest(AuthenticationController.class)
@SuppressWarnings("removal")
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private AuthenticationService authenticationService;
    
    @MockBean
    private JwtServices jwtService;
    
    @Test
    public void testUserRegistration_Success() throws Exception {
        // Arrange
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setFullName("Test User");
        request.setEmail("test@example.com");
        request.setPhone("1234567890");
        request.setPassword("Password123");
        
        Map<String, String> response = new HashMap<>();
        response.put("token", "test-jwt-token");
        response.put("message", "User registered successfully");
        
        when(authenticationService.signup(any(RegisterRequestDTO.class))).thenReturn((User) response);
        
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("test-jwt-token"))
            .andExpect(jsonPath("$.message").value("User registered successfully"));
    }
    
    @Test
    public void testUserLogin_Success() throws Exception {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmailOrPhone("test@example.com");
        request.setPassword("Password123");
        
        Map<String, String> response = new HashMap<>();
        response.put("token", "test-jwt-token");
        response.put("message", "Login successful");
        
        when(authenticationService.authenticate(any(LoginRequestDTO.class)))
            .thenReturn((User) response);
        
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("test-jwt-token"))
            .andExpect(jsonPath("$.message").value("Login successful"));
    }
}