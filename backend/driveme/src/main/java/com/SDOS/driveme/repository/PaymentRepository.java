package com.sdos.driveme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.sdos.driveme.model.Payment;

@EnableJpaRepositories
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // List<Payment> findByBookingBookingId(Long bookingId);
    // List<Payment> findByStatus(String status);
}