package com.example.driveme.DTO;

import java.time.LocalDateTime;

public class ErrorResponseDTO {
    private String message;
    private String error;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDTO(String message, String error, int status) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public ErrorResponseDTO setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getError() {
        return error;
    }

    public ErrorResponseDTO setError(String error) {
        this.error = error;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public ErrorResponseDTO setStatus(int status) {
        this.status = status;
        return this;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}