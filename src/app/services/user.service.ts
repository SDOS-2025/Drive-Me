import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface BookingSummary {
  bookingId: number;
  pickupLocation: string;
  dropoffLocation: string;
  createdAt: string;
  status: string;
  fare: number;
  driverName?: string;
  vehicleType?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserBookingService {
  private apiUrl = 'http://localhost:8080'; // Your backend URL

  constructor(private http: HttpClient) { }

  // Get all bookings for the current user
  getUserBookings(): Observable<BookingSummary[]> {
    // Get token from localStorage
    const token = localStorage.getItem('token');
    
    // Create headers with Bearer token
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<BookingSummary[]>(`${this.apiUrl}/bookings/user`, { headers });
  }

  // Get a specific booking by ID
  getBookingDetails(id: number): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<any>(`${this.apiUrl}/bookings/${id}`, { headers });
  }
  
  // Cancel a booking
  cancelBooking(id: number): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.put<any>(`${this.apiUrl}/bookings/${id}/cancel`, {}, { headers });
  }
}