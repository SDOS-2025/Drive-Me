import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface BookingRequest {
  customer: { id: number };
  driver: { driver_id: number };
  vehicle: { id: number };
  pickupLocation: string;
  dropoffLocation: string;
  pickupDateTime?: string;
  fare: number;
  estimatedDuration?: number;
}

export interface BookingSummary {
  bookingId: number;
  pickupLocation: string;
  dropoffLocation: string;
  createdAt: string;
  status: string;
  fare: number;
  driverName?: string;
  vehicleModel?: string;
  distance?: number;
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
    const headers = this.getAuthHeaders();
    return this.http.get<any[]>(`${this.apiUrl}/driver/available`, { headers });
  }
  
  // Create a booking
  createBooking(booking: BookingRequest, paymentScreenshot: File): Observable<any> {
    const token = localStorage.getItem('token');
  
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
      // DO NOT set 'content-type' manually here
    });
  
    const formData = new FormData();
    formData.append('bookingRequest', new Blob(
      [JSON.stringify(booking)],
      { type: 'application/json' }
    ));
    formData.append('paymentScreenshot', paymentScreenshot);
  
    return this.http.post<any>(`${this.apiUrl}/bookings`, formData, { headers });
  }
   
  // Get all bookings for the current user
  getUserBookings(): Observable<BookingSummary[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<BookingSummary[]>(`${this.apiUrl}/bookings/user`, { headers });
  }
  
  // Get user's confirmed bookings
  getConfirmedBookings(): Observable<BookingSummary[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<BookingSummary[]>(`${this.apiUrl}/bookings/user/confirmed`, { headers });
  }
  
  // Get user's cancelled bookings
  getCancelledBookings(): Observable<BookingSummary[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<BookingSummary[]>(`${this.apiUrl}/bookings/user/cancelled`, { headers });
  }
  
  // Get user's completed bookings
  getCompletedBookings(): Observable<BookingSummary[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<BookingSummary[]>(`${this.apiUrl}/bookings/user/completed`, { headers });
  }
  
  // Cancel a booking
  cancelBooking(bookingId: number, reason?: string): Observable<any> {
    const headers = this.getAuthHeaders();
    const payload = reason ? { reason } : {};
    return this.http.put<any>(`${this.apiUrl}/bookings/${bookingId}/cancel`, payload, { headers });
  }
  
  // Update booking status (admin/driver operation)
  updateBookingStatus(bookingId: number, status: string, distance?: number): Observable<any> {
    const headers = this.getAuthHeaders();
    const payload: any = { status };
    
    if (status === 'COMPLETED' && distance !== undefined) {
      payload.distance = distance;
    }
    
    return this.http.put<any>(`${this.apiUrl}/bookings/${bookingId}/status`, payload, { headers });
  }

  // Update review
  updateReview(bookingId: number, rating: number, review: string): Observable<any> {
    const headers = this.getAuthHeaders();
    if (review === '') {
      review = 'No review provided';
    }
    const payload = { 
      status: "COMPLETED",
      driverRating: rating, 
      feedback: review };
    return this.http.put<any>(`${this.apiUrl}/bookings/${bookingId}/status`, payload, { headers });
  }
  
  // Get all bookings (admin operation)
  getAllBookings(): Observable<BookingSummary[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<BookingSummary[]>(`${this.apiUrl}/bookings/all`, { headers });
  }
  
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }
}