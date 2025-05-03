package com.SDOS.driveme.Exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.SDOS.driveme.DTO.ErrorResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException ex) {
        logger.error("Authentication error: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorResponseDTO(ex.getMessage(), "Authentication failed", HttpStatus.UNAUTHORIZED.value()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExistsException(UserException ex) {
        logger.error("Registration error: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorResponseDTO(ex.getMessage(), "Registration failed", HttpStatus.CONFLICT.value()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidTokenException(InvalidTokenException ex) {
        logger.error("Token error: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorResponseDTO(ex.getMessage(), "Invalid token", HttpStatus.UNAUTHORIZED.value()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.error("Validation errors: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        logger.error("Unexpected error: ", ex);
        return new ResponseEntity<>(
                new ErrorResponseDTO("An unexpected error occurred", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}