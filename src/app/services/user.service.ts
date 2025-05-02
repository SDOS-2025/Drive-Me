import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface BookingSummary {
  bookingId: number;
  userId?: number; // Add this as optional with the ? symbol
  driverId: number;
  driverName: string;
  pickupLocation: string;
  dropoffLocation: string;
  pickupTime: string;
  status: string;
  fare: number;
  createdAt: string;
  updatedAt: string;
}

export interface UserDetails {
  userId: number;
  fullName: string;
  email: string;
  phone: string;
  aadharCard: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserBookingService {
  private apiUrl = 'http://localhost:8080'; // Your backend URL

  constructor(private http: HttpClient) { }

  // Get User's profile details
  getUserProfile(): Observable<UserDetails> {
    const headers = this.getAuthHeaders();
    return this.http.get<UserDetails>(`${this.apiUrl}/users/my-profile`, { headers });
  }

  // Get profile picture
  getProfilePicture(filename: String): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get(`${this.apiUrl}/users/my-profile/profile/${filename}`, { headers, responseType: 'blob' });
  }

  // Update User's profile
  updateUserProfile(userData: any, profilePicture: File): Observable<any> {
    const headers = {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }

    const formData = new FormData();
    formData.append('userDetails', new Blob(
      [JSON.stringify(userData)],
      { type: 'application/json' }
    ));

    if (profilePicture) {
      formData.append('profilePicture', profilePicture);
    }

    return this.http.put<any>(`${this.apiUrl}/users/my-profile/update`, formData, { headers });
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
  
  // Get all users (admin operation)
  getAllUsers(): Observable<UserDetails[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<UserDetails[]>(`${this.apiUrl}/users`, { headers });
  }
  
  // Helper method for authorization headers
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }
}