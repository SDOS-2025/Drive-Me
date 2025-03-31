package com.example.driveme.repository;

import com.example.driveme.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // List<Payment> findByBookingBookingId(Long bookingId);
    // List<Payment> findByStatus(String status);
}