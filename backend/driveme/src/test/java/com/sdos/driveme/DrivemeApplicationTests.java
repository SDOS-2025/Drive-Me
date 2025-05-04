package com.sdos.driveme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.sdos.driveme.model.User;
import com.sdos.driveme.repository.UserRepository;

@SpringBootTest
@SuppressWarnings("removal")
@ActiveProfiles("test")
public class DrivemeApplicationTests {

    @MockBean
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        // Basic test to verify the context loads
    }

    @Test
    void testUserRepositoryFindByIdNotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // Act
        Optional<User> result = userRepository.findById(1L);
        
        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
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
    void testUserRepositoryFindById() {
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
}