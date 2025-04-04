package com.example.driveme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.driveme.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}