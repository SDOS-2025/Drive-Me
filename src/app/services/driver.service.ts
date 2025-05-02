import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DriverDetails {
  driverId: number;
  name: string;
  email: string;
  phone: string;
  aadharCard: string;
  licenseNumber: string;
}

export interface DriverStatus {
  driverId: number;
  name: string;
  phone: string;
  status: string;
  currentLocation?: string;
  currentTrip?: any;
  averageRating?: number;
  totalTrips?: number;
}

export interface DriverTrip {
  bookingId: number;
  pickupLocation: string;
  dropoffLocation: string;
  status: string;
  createdAt: string;
  fare: number;
  averageRating?: number;
  customerName?: string;
  customerPhone?: string;
}

@Injectable({
  providedIn: 'root'
})
export class DriverService {
  private apiUrl = 'http://localhost:8080'; // Your backend URL

  constructor(private http: HttpClient) { }

  // Get all drivers
  getDriverList(): Observable<DriverDetails[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<DriverDetails[]>(`${this.apiUrl}/driver`, { headers });
  }
  
  // Get driver status information
  getDriverStatus(): Observable<DriverStatus[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<DriverStatus[]>(`${this.apiUrl}/driver/status`, { headers });
  }
  
  // Get a specific driver by ID
  getDriverById(driverId: number): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.apiUrl}/driver/${driverId}`, { headers });
  }
  
  // Update driver status
  updateDriverStatus(driverId: number, status: string, location?: string): Observable<any> {
    const headers = this.getAuthHeaders();
    const payload: any = { status };
    
    if (location) {
      payload.location = location;
    }
    
    return this.http.put<any>(`${this.apiUrl}/driver/${driverId}/status`, payload, { headers });
  }
  
  // Get driver's bookings
  getDriverBookings(): Observable<DriverTrip[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<DriverTrip[]>(`${this.apiUrl}/driver/my-bookings`, { headers });
  }
  
  // Get available drivers
  getAvailableDrivers(): Observable<DriverStatus[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<DriverStatus[]>(`${this.apiUrl}/driver/available`, { headers });
  }
  
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }
}