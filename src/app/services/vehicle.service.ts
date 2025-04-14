import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface BackendVehicle {
  vehicleId: number;
  model: string;
  registrationNumber: string;
  carNumber: string;
  userId: number;
  vehicleType?: string;
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

    return this.http.get<BackendVehicle[]>(`${this.apiUrl}/users/my-vehicles`, { headers });
  }

  // Get all vehicles
  getAllVehicles(): Observable<BackendVehicle[]> {
    const token = localStorage.getItem('token');
    const headers = this.getAuthHeaders(token);

    return this.http.get<BackendVehicle[]>(`${this.apiUrl}/users/vehicles`, { headers });
  }

  // Add a new vehicle
  addVehicle(userId: number, vehicleData: any): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = this.getAuthHeaders(token);
    
    // Map frontend vehicle to backend format
    const backendVehicle = {
        model: vehicleData.model,
        registrationNumber: vehicleData.registrationNumber,
        carNumber: vehicleData.carNumber,
        vehicleType: vehicleData.vehicleType || 'SEDAN',
        userId: userId,
    };

    return this.http.post(`${this.apiUrl}/users/add-vehicle`, backendVehicle, { headers });
  }

  // Delete a vehicle
  removeVehicle(vehicleId: number): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = this.getAuthHeaders(token);

    return this.http.delete(`${this.apiUrl}/users/vehicles/${vehicleId}`, { headers });
  }

  // Helper method for authorization headers
  private getAuthHeaders(token: string | null): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }
}