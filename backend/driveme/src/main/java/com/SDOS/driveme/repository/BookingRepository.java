package com.SDOS.driveme.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SDOS.driveme.model.Booking;
import com.SDOS.driveme.model.Driver;
import com.SDOS.driveme.model.User;
import com.SDOS.driveme.model.Booking.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomer(User user);
    List<Booking> findByCustomerAndStatus(User user, BookingStatus status);
    List<Booking> findByDriverAndStatusIn(Driver driver, List<BookingStatus> status);
    List<Booking> findByDriver(Driver driver);
    List<Booking> findByStatusAndDriverIsNull(BookingStatus status);
    List<Booking> findByDriverAndStatus(Driver driver, BookingStatus status);
}