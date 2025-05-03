import requests
import json
import time
import random
from datetime import datetime, timedelta
import os
from io import BytesIO
from PIL import Image, ImageDraw, ImageFont

# Base URL for the API
BASE_URL = "http://localhost:8080"

# Store tokens and IDs
users = []
drivers = []
vehicles = []
bookings = []

# Helper functions
def create_payment_screenshot(booking_id, amount):
    """Create a simple payment screenshot image with text"""
    img = Image.new('RGB', (800, 600), color=(255, 255, 255))
    d = ImageDraw.Draw(img)
    d.text((10, 10), f"Payment Receipt", fill=(0, 0, 0))
    d.text((10, 50), f"Booking ID: {booking_id}", fill=(0, 0, 0))
    d.text((10, 90), f"Amount: ₹{amount}", fill=(0, 0, 0))
    d.text((10, 130), f"Date: {datetime.now().strftime('%Y-%m-%d %H:%M')}", fill=(0, 0, 0))
    d.text((10, 170), f"Status: PAID", fill=(0, 0, 0))
    
    # Save to BytesIO object
    img_byte_arr = BytesIO()
    img.save(img_byte_arr, format='JPEG')
    img_byte_arr.seek(0)
    
    return img_byte_arr

def register_user(full_name, email, phone, aadhar_card, password):
    url = f"{BASE_URL}/auth/user/signup"
    data = {
        "fullName": full_name,
        "email": email,
        "phone": phone,
        "aadharCard": aadhar_card,
        "password": password,
    }
    
    response = requests.post(url, json=data)
    if response.status_code == 200:
        user_data = response.json()
        print(f"Registered user: {full_name}")
        return user_data
    else:
        print(f"Failed to register user {full_name}: {response.text}")
        return None

def register_driver(name, email, phone, aadhar_card, license_number, password):
    url = f"{BASE_URL}/auth/driver/signup"
    data = {
        "fullName": name,
        "email": email,
        "phone": phone,
        "aadharCard": aadhar_card,
        "licenseNumber": license_number,
        "password": password
    }
    
    response = requests.post(url, json=data)
    if response.status_code == 200:
        driver_data = response.json()
        print(f"Registered driver: {name}")
        return driver_data
    else:
        print(f"Failed to register driver {name}: {response.text}")
        return None

def login_user(email, password):
    url = f"{BASE_URL}/auth/user/login"
    data = {
        "emailOrPhone": email,
        "password": password
    }
    
    response = requests.post(url, json=data)
    if response.status_code == 200:
        login_data = response.json()
        print(f"Logged in user: {email}")
        return login_data
    else:
        print(f"Failed to login user {email}: {response.text}")
        return None

def login_driver(email, password):
    url = f"{BASE_URL}/auth/driver/login"
    data = {
        "emailOrPhone": email,
        "password": password
    }
    
    response = requests.post(url, json=data)
    if response.status_code == 200:
        login_data = response.json()
        print(f"Logged in driver: {email}")
        return login_data
    else:
        print(f"Failed to login driver {email}: {response.text}")
        return None

def add_vehicle(user_id, token, model, registration_number, car_number, vehicle_type, manufacture_year=2020, color="White"):
    url = f"{BASE_URL}/users/add-vehicle"
    headers = {"Authorization": f"Bearer {token}"}
    data = {
        "userId": user_id,
        "model": model,
        "registrationNumber": registration_number,
        "carNumber": car_number,
        "vehicleType": vehicle_type,
        "manufactureYear": manufacture_year,
        "color": color
    }
    
    response = requests.post(url, json=data, headers=headers)
    if response.status_code == 201:
        print(f"Added vehicle: {model} - {registration_number}")
        return True
    else:
        print(f"Failed to add vehicle {model}: {response.text}")
        return False

def create_booking(user_token, customer_id, driver_id, vehicle_id, pickup_location, dropoff_location, 
                  pickup_date_time, fare, status="PENDING", estimated_duration=3):
    url = f"{BASE_URL}/bookings"
    headers = {
        "Authorization": f"Bearer {user_token}"
    }
    
    # Create booking data
    booking_request = {
        "customer": {"id": customer_id},
        "driver": {"driver_id": driver_id},
        "vehicle": {"id": vehicle_id},
        "pickupLocation": pickup_location,
        "dropoffLocation": dropoff_location,
        "pickupDateTime": pickup_date_time,
        "fare": fare,
        "estimatedDuration": estimated_duration
    }
    
    # Create payment screenshot
    payment_screenshot = create_payment_screenshot(random.randint(10000, 99999), fare)
    
    # Create multipart form data
    files = {
        'bookingRequest': (None, json.dumps(booking_request), 'application/json'),
        'paymentScreenshot': ('payment.jpg', payment_screenshot, 'image/jpeg')
    }
    
    response = requests.post(url, headers=headers, files=files)
    if response.status_code == 200:
        print(f"Created booking from {pickup_location} to {dropoff_location}")
        booking_data = response.json()
        return booking_data
    else:
        print(f"Failed to create booking: {response.text}")
        return None

def update_booking_status(token, booking_id, status):
    url = f"{BASE_URL}/bookings/{booking_id}/status"
    headers = {"Authorization": f"Bearer {token}"}
    data = {"status": status}
    
    response = requests.put(url, json=data, headers=headers)
    if response.status_code == 200:
        print(f"Updated booking {booking_id} status to {status}")
        return True
    else:
        print(f"Failed to update booking status: {response.text}")
        return False

def rate_booking(user_token, booking_id, rating, feedback):
    url = f"{BASE_URL}/bookings/{booking_id}/rate"
    headers = {"Authorization": f"Bearer {user_token}"}
    data = {
        "rating": rating,
        "feedback": feedback
    }
    
    response = requests.post(url, json=data, headers=headers)
    if response.status_code == 200:
        print(f"Rated booking {booking_id} with {rating} stars")
        return True
    else:
        print(f"Failed to rate booking: {response.text}")
        return False


def main():
    # 1. Register users (1 superuser + 5 regular users)
    user_data = [
        {"full_name": "Admin User", "email": "admin@driveme.com", "phone": "9876543210", "aadhar_card": "123456789012", "password": "Admin@123"},
        {"full_name": "Priya Sharma", "email": "priya@example.com", "phone": "9876543211", "aadhar_card": "123456789013", "password": "Password@123"},
        {"full_name": "Rahul Patel", "email": "rahul@example.com", "phone": "9876543212", "aadhar_card": "123456789014", "password": "Password@123"},
        {"full_name": "Anjali Gupta", "email": "anjali@example.com", "phone": "9876543213", "aadhar_card": "123456789015", "password": "Password@123"},
        {"full_name": "Vivek Singh", "email": "vivek@example.com", "phone": "9876543214", "aadhar_card": "123456789016", "password": "Password@123"},
        {"full_name": "Deepika Verma", "email": "deepika@example.com", "phone": "9876543215", "aadhar_card": "123456789017", "password": "Password@123"}
    ]
    
    for user in user_data:
        user_login = login_user(user["email"], user["password"])
        if user_login:
            users.append({
                "id": user_login["userId"],
                "name": user["full_name"],
                "email": user["email"],
                "token": user_login["token"],
                "password": user["password"]
            })
    
    # 2. Register drivers (5)
    driver_data = [
        {"name": "Vikram Malhotra", "email": "vikram@driver.com", "phone": "8765432101", "aadhar_card": "987654321012", "license_number": "DL12345678", "password": "Driver@123"},
        {"name": "Rajesh Kumar", "email": "rajesh@driver.com", "phone": "8765432102", "aadhar_card": "987654321013", "license_number": "DL23456789", "password": "Driver@123"},
        {"name": "Sunil Sharma", "email": "sunil@driver.com", "phone": "8765432103", "aadhar_card": "987654321014", "license_number": "DL34567890", "password": "Driver@123"},
        {"name": "Amit Joshi", "email": "amit@driver.com", "phone": "8765432104", "aadhar_card": "987654321015", "license_number": "DL45678901", "password": "Driver@123"},
        {"name": "Nitin Mehta", "email": "nitin@driver.com", "phone": "8765432105", "aadhar_card": "987654321016", "license_number": "DL56789012", "password": "Driver@123"}
    ]
    
    for driver in driver_data:
        driver_login = login_driver(driver["email"], driver["password"])
        if driver_login:
            drivers.append({
                "id": driver_login["userId"],
                "name": driver["name"],
                "email": driver["email"],
                "token": driver_login["token"],
                "license": driver["license_number"],
                "password": driver["password"]
            })
    
    print(users)
    # 3. Add vehicles for users
    vehicle_data = [
        {"user_id": users[1]["id"], "model": "Honda City", "registration_number": "MH02AB1234", "car_number": "MH02AB1234", "vehicle_type": "SEDAN", "color": "White"},
        {"user_id": users[1]["id"], "model": "Maruti Swift", "registration_number": "MH02CD5678", "car_number": "MH02CD5678", "vehicle_type": "HATCHBACK", "color": "Red"},
        {"user_id": users[2]["id"], "model": "Toyota Fortuner", "registration_number": "DL01PQ7890", "car_number": "DL01PQ7890", "vehicle_type": "SUV", "color": "Black"},
        {"user_id": users[3]["id"], "model": "Hyundai Creta", "registration_number": "KA01RS2345", "car_number": "KA01RS2345", "vehicle_type": "SUV", "color": "Silver"},
        {"user_id": users[4]["id"], "model": "Tata Nexon", "registration_number": "UP80TU6789", "car_number": "UP80TU6789", "vehicle_type": "SUV", "color": "Blue"},
        {"user_id": users[5]["id"], "model": "Honda Civic", "registration_number": "GJ01VW1290", "car_number": "GJ01VW1290", "vehicle_type": "SEDAN", "color": "Grey"},
        {"user_id": users[2]["id"], "model": "Maruti Baleno", "registration_number": "DL10XY3456", "car_number": "DL10XY3456", "vehicle_type": "HATCHBACK", "color": "White"},
        {"user_id": users[3]["id"], "model": "Kia Seltos", "registration_number": "KA05ZA7890", "car_number": "KA05ZA7890", "vehicle_type": "SUV", "color": "Red"}
    ]
    
    for vehicle in vehicle_data:
        user_index = next((i for i, u in enumerate(users) if u["id"] == vehicle["user_id"]), -1)
        if user_index != -1:
            vehicles.append({
                "id": len(vehicles) + 1,  # Approximation - ideally would get the actual ID from response
                "user_id": vehicle["user_id"],
                "model": vehicle["model"],
                "registration_number": vehicle["registration_number"]
            })
    
    # 4. Create completed bookings (3 per user)
    completed_bookings_data = []
    
    # Generate 3 completed bookings for each regular user
    for user_index in range(1, len(users)):
        user = users[user_index]
        user_vehicles = [v for v in vehicles if v["user_id"] == user["id"]]
        
        if not user_vehicles:
            continue
            
        for i in range(3):
            # Get random driver and vehicle
            driver = random.choice(drivers)
            vehicle = random.choice(user_vehicles)
            
            # Get past date (10-30 days ago)
            days_ago = random.randint(10, 30)
            pickup_date = (datetime.now() - timedelta(days=days_ago)).strftime("%Y-%m-%d %H:%M")
            
            # Random locations
            locations = [
                ("Andheri East, Mumbai", "Bandra West, Mumbai"),
                ("Connaught Place, Delhi", "Noida Sector 62"),
                ("Indiranagar, Bangalore", "Electronic City, Bangalore"),
                ("Hazratganj, Lucknow", "Gomti Nagar, Lucknow"),
                ("Navrangpura, Ahmedabad", "Ahmedabad Airport")
            ]
            pickup, dropoff = random.choice(locations)
            
            # Random fare between 200-1000
            fare = random.randint(200, 1000)
            
            booking_data = {
                "user_id": user["id"],
                "user_token": user["token"],
                "driver_id": driver["id"],
                "vehicle_id": vehicle["id"],
                "pickup_location": pickup,
                "dropoff_location": dropoff,
                "pickup_date_time": pickup_date,
                "fare": fare,
                "status": "COMPLETED"
            }
            completed_bookings_data.append(booking_data)
    
    # Create the completed bookings
    for booking_data in completed_bookings_data:
        booking = create_booking(
            booking_data["user_token"],
            booking_data["user_id"],
            booking_data["driver_id"],
            booking_data["vehicle_id"],
            booking_data["pickup_location"],
            booking_data["dropoff_location"],
            booking_data["pickup_date_time"],
            booking_data["fare"]
        )
        
        if booking:
            booking_id = booking.get("bookingId")
            if booking_id:
                # Update to CONFIRMED
                update_booking_status(booking_data["user_token"], booking_id, "CONFIRMED")
                # Update to COMPLETED
                update_booking_status(booking_data["user_token"], booking_id, "COMPLETED")
                # Add rating (4-5 stars)
                rating = random.randint(4, 5)
                feedback_options = [
                    "Very professional and punctual",
                    "Excellent driving skills",
                    "Very safe and comfortable ride",
                    "Good service, would recommend",
                    "Outstanding experience"
                ]
                rate_booking(booking_data["user_token"], booking_id, rating, random.choice(feedback_options))
                bookings.append({
                    "id": booking_id,
                    "status": "COMPLETED",
                    "user_id": booking_data["user_id"]
                })
    
    # 5. Create one ongoing trip per user
    for user_index in range(1, len(users)):
        user = users[user_index]
        user_vehicles = [v for v in vehicles if v["user_id"] == user["id"]]
        
        if not user_vehicles:
            continue
            
        # Get random driver and vehicle
        driver = random.choice(drivers)
        vehicle = random.choice(user_vehicles)
        
        # Today's date
        pickup_date = datetime.now().strftime("%Y-%m-%d %H:%M")
        
        # Random locations
        locations = [
            ("Juhu Beach, Mumbai", "Chhatrapati Shivaji Terminus"),
            ("India Gate, Delhi", "Delhi University North Campus"),
            ("Cubbon Park, Bangalore", "Bangalore Palace"),
            ("Chowk, Lucknow", "Ambedkar Memorial Park"),
            ("Kankaria Lake, Ahmedabad", "Science City, Ahmedabad")
        ]
        pickup, dropoff = random.choice(locations)
        
        # Random fare between 300-800
        fare = random.randint(300, 800)
        
        booking = create_booking(
            user["token"],
            user["id"],
            driver["id"],
            vehicle["id"],
            pickup,
            dropoff,
            pickup_date,
            fare
        )
        
        if booking:
            booking_id = booking.get("bookingId")
            if booking_id:
                # Update to CONFIRMED (ongoing)
                update_booking_status(user["token"], booking_id, "CONFIRMED")
                bookings.append({
                    "id": booking_id,
                    "status": "CONFIRMED",
                    "user_id": user["id"]
                })
    
    # 6. Create 3 pending bookings (available for drivers)
    pending_locations = [
        ("Lower Parel, Mumbai", "Mumbai Airport"),
        ("Saket, Delhi", "Red Fort, Delhi"),
        ("JP Nagar, Bangalore", "Bangalore Airport")
    ]
    
    for i in range(3):
        user_index = random.randint(1, len(users) - 1)
        user = users[user_index]
        user_vehicles = [v for v in vehicles if v["user_id"] == user["id"]]
        
        if not user_vehicles:
            continue
            
        vehicle = random.choice(user_vehicles)
        pickup, dropoff = pending_locations[i]
        
        # Tomorrow's date
        pickup_date = (datetime.now() + timedelta(days=1)).strftime("%Y-%m-%d %H:%M")
        
        # Random fare between 400-1000
        fare = random.randint(400, 1000)
        
        # Create booking with no driver (PENDING)
        booking = create_booking(
            user["token"],
            user["id"],
            None,  # No driver yet
            vehicle["id"],
            pickup,
            dropoff,
            pickup_date,
            fare
        )
        
        if booking:
            booking_id = booking.get("bookingId")
            if booking_id:
                bookings.append({
                    "id": booking_id,
                    "status": "PENDING",
                    "user_id": user["id"]
                })
    
    # 7. Create 2 cancelled bookings per user
    for user_index in range(1, len(users)):
        user = users[user_index]
        user_vehicles = [v for v in vehicles if v["user_id"] == user["id"]]
        
        if not user_vehicles:
            continue
            
        for i in range(2):
            # Get random driver and vehicle
            driver = random.choice(drivers)
            vehicle = random.choice(user_vehicles)
            
            # Random past date (5-15 days ago)
            days_ago = random.randint(5, 15)
            pickup_date = (datetime.now() - timedelta(days=days_ago)).strftime("%Y-%m-%d %H:%M")
            
            # Random locations
            locations = [
                ("Goregaon, Mumbai", "Vashi, Navi Mumbai"),
                ("Vasant Kunj, Delhi", "Ghaziabad"),
                ("Jayanagar, Bangalore", "Yelahanka"),
                ("Indira Nagar, Lucknow", "Barabanki"),
                ("Vastrapur, Ahmedabad", "Rajkot")
            ]
            pickup, dropoff = random.choice(locations)
            
            # Random fare between 500-2000
            fare = random.randint(500, 2000)
            
            booking = create_booking(
                user["token"],
                user["id"],
                driver["id"],
                vehicle["id"],
                pickup,
                dropoff,
                pickup_date,
                fare
            )
            
            if booking:
                booking_id = booking.get("bookingId")
                if booking_id:
                    # First confirm, then cancel
                    update_booking_status(user["token"], booking_id, "CONFIRMED")
                    update_booking_status(user["token"], booking_id, "CANCELLED")
                    bookings.append({
                        "id": booking_id,
                        "status": "CANCELLED",
                        "user_id": user["id"]
                    })

    print("\nDatabase population complete!")
    print(f"Created {len(users)} users")
    print(f"Created {len(drivers)} drivers")
    print(f"Created {len(vehicles)} vehicles")
    print(f"Created {len(bookings)} bookings")

if __name__ == "__main__":
    main()