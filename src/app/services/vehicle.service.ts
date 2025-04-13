import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, Observable } from 'rxjs';

export interface BackendVehicle {
  id: number;
  model: string;
  registerationNumber: string;
  carNumber: string;
  userId: number;
}

@Injectable({
  providedIn: 'root'
})
export class VehicleService {
  private apiUrl = 'http://localhost:8080'; // Your backend URL

  constructor(private http: HttpClient) { }

  // Get all vehicles for the current user
  getUserVehicles(): Observable<BackendVehicle[]> {
    // Get token from localStorage
    const token = localStorage.getItem('token');
    
    // Create headers with Bearer token
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<BackendVehicle[]>(`${this.apiUrl}/users/vehicles`, { headers });
  }

  // Add a new vehicle
  addVehicle(userId: number, vehicleData: any): Observable<any> {
    const token = localStorage.getItem('token');
    
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
    
    // Map frontend vehicle to backend format
    const backendVehicle = {
        model: vehicleData.model,
        registration_number: vehicleData.registerationNumber,
        car_number: vehicleData.carNumber,
        user_id: userId,
    };

    return this.http.post(`${this.apiUrl}/users/add-vehicle`, backendVehicle, { 
        headers, 
      });
  }

  // Delete a vehicle
  removeVehicle(vehicleId: number): Observable<any> {
    const token = localStorage.getItem('token');
    
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.delete(`${this.apiUrl}/users/vehicles/${vehicleId}`, { headers });
  }
}