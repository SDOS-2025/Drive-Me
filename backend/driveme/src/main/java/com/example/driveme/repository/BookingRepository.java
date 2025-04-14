package com.example.driveme.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.driveme.model.Booking;
import com.example.driveme.model.Booking.BookingStatus;
import com.example.driveme.model.User;
import com.example.driveme.model.Driver;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomer(User user);
    List<Booking> findByCustomerAndStatus(User user, BookingStatus status);
    List<Booking> findByDriverAndStatusIn(Driver driver, List<BookingStatus> status);
    List<Booking> findByDriver(Driver driver);
    List<Booking> findByDriverAndStatus(Driver driver, BookingStatus status);
}