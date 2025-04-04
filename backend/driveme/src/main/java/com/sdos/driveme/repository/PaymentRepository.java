package com.sdos.driveme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sdos.driveme.model.Payment;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // List<Payment> findByBookingBookingId(Long bookingId);
    // List<Payment> findByStatus(String status);
}