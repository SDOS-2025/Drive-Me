package com.sdos.driveme.JwtAuth;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sdos.driveme.repository.DriverRepository;
import com.sdos.driveme.repository.UserRepository;

@Configuration
public class ApplicationConfiguration {
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    
    public ApplicationConfiguration(UserRepository userRepository, DriverRepository driverRepository) {
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
    }

    @Bean
    UserDetailsService userDetailsService() {
        if (userRepository == null) {
            throw new IllegalStateException("UserRepository is not initialized");
        }
        
        return username -> userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhone(username))
                .or(() -> userRepository.findByFullName(username))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Bean
    UserDetailsService driverDetailsService() {
        if (driverRepository == null) {
            throw new IllegalStateException("DriverRepository is not initialized");
        }
        
        return username -> driverRepository.findByEmail(username)
                .or(() -> driverRepository.findByPhone(username))
                .or(() -> driverRepository.findByName(username))
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return new ProviderManager(Arrays.asList(
            userAuthenticationProvider(),
            driverAuthenticationProvider()
        ));
    }

    @Bean
    AuthenticationProvider userAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    AuthenticationProvider driverAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(driverDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}