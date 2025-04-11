// src/app/services/booking.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface BookingRequest {
  customer: { id: number };
  driver: { driver_id: number };
  vehicle: { id: number };
  pickupLocation: string;
  dropoffLocation: string;
  fare: number;
}

export interface PaymentRequest {
  booking: { bookingId: number };
  amount: number;
  paymentMethod: string;
}

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private apiUrl = 'http://localhost:8080'; // Your backend URL

  constructor(private http: HttpClient) { }
  
  // Get available drivers
  getAvailableDrivers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/driver`);
  }
  
  // Create a booking
  createBooking(booking: BookingRequest): Observable<any> {
    // setting authorization header
    const headers = { 'Authorization': `Bearer ${localStorage.getItem('token')}` };

    return this.http.post<any>(`${this.apiUrl}/bookings`, booking, { headers });
  }
}