import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DriverDetails {
    id: number;
    fullName: string;
    email: string;
    phone: string;
    aadharNumber: string;
}

@Injectable({
  providedIn: 'root'
})
export class DriverService {
  private apiUrl = 'http://localhost:8080'; // Your backend URL

  constructor(private http: HttpClient) { }

  // Get all bookings for the current user
  getDriverList(): Observable<DriverDetails[]> {
    // Get token from localStorage
    const token = localStorage.getItem('token');
    
    // Create headers with Bearer token
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<DriverDetails[]>(`${this.apiUrl}/driver`, { headers });
  }
}