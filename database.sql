-- Drop database if it exists (optional)
DROP DATABASE IF EXISTS driveme;

-- Create the database
CREATE DATABASE driveme;

use driveme;

-- Create Users Table
CREATE TABLE users (
    user_id INT PRIMARY KEY NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    user_type VARCHAR(10) CHECK (user_type IN ('customer', 'driver')) NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Drivers Table
CREATE TABLE drivers (
    driver_id INT PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    experience INTEGER CHECK (experience >= 0),
    rating DECIMAL(3,2) DEFAULT 5.0 CHECK (rating BETWEEN 1 AND 5)
);

-- Create Vehicles Table
CREATE TABLE vehicles (
    vehicle_id INT PRIMARY KEY NOT NULL,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    model VARCHAR(100) NOT NULL,
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Bookings Table
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY NOT NULL,
    customer_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    driver_id INT REFERENCES drivers(driver_id) ON DELETE SET NULL,
    vehicle_id INT REFERENCES vehicles(vehicle_id) ON DELETE CASCADE,
    pickup_location TEXT NOT NULL,
    dropoff_location TEXT NOT NULL,
    status VARCHAR(15) CHECK (status IN ('pending', 'accepted', 'completed', 'cancelled')) DEFAULT 'pending',
    fare NUMERIC(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

-- Create Payments Table
CREATE TABLE payments (
    payment_id INT PRIMARY KEY NOT NULL,
    booking_id INT REFERENCES bookings(booking_id) ON DELETE CASCADE,
    amount NUMERIC(10,2) NOT NULL,
    payment_method VARCHAR(20) CHECK (payment_method IN ('cash', 'card', 'UPI')) NOT NULL,
    status VARCHAR(15) CHECK (status IN ('pending', 'successful', 'failed')) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);