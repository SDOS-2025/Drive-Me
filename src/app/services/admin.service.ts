import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AdminService {

    private apiUrl = 'http://localhost:8080' // Replace with your API URL
    constructor(private http: HttpClient) { }

    // Dashboard statistics
    getStats(): Observable<any> {
        const headers = this.getAuthHeaders();
        return this.http.get(`${this.apiUrl}/admin/dashboard`, { headers });
    }

    // User management
    getAllUsers(): Observable<any> {
        const headers = this.getAuthHeaders();
        return this.http.get(`${this.apiUrl}/admin/users`, { headers });
    }

    updateUser(userId: number, userData: any): Observable<any> {
        const headers = this.getAuthHeaders();
        return this.http.put(`${this.apiUrl}/admin/update-user/${userId}`, { headers }, userData);
    }

    deleteUser(userId: number): Observable<any> {
        const headers = this.getAuthHeaders();
        return this.http.delete(`${this.apiUrl}/admin/delete-user/${userId}`, { headers });
    }

    // Driver management
    getAllDrivers(): Observable<any> {
        const headers = this.getAuthHeaders();
        return this.http.get(`${this.apiUrl}/admin/drivers`, { headers });
    }

    updateDriver(driverId: number, driverData: any): Observable<any> {
        const headers = this.getAuthHeaders();
        return this.http.put(`${this.apiUrl}/admin/update-driver/${driverId}`,  driverData, { headers });
    }

    deleteDriver(driverId: number): Observable<any> {
        const headers = this.getAuthHeaders();
        return this.http.delete(`${this.apiUrl}/admin/delete-driver/${driverId}`, { headers });
    }

    // Booking management
    getAllBookings(): Observable<any> {
        const headers = this.getAuthHeaders();
        return this.http.get(`${this.apiUrl}/admin/bookings`, { headers });
    }

    getBookingDetails(bookingId: number): Observable<any> {
        const headers = this.getAuthHeaders();
        return this.http.get(`${this.apiUrl}/admin/bookings/${bookingId}`, { headers });
    }

    private getAuthHeaders(): HttpHeaders {
        const token = localStorage.getItem('token');
        console.log('token', token);
        return new HttpHeaders({
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        });
      }
}