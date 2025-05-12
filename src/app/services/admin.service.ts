import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AdminService {

    private apiUrl = 'https://driveme-app-latest.onrender.com' // Replace with your API URL
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
        return this.http.put(`${this.apiUrl}/admin/update-driver/${driverId}`, driverData, { headers });
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

    getImageUrl(filename: string): Observable<any> {
        const token = localStorage.getItem('token');
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`
        });
        return this.http.get(`${this.apiUrl}/admin/payment-screenshots/${filename}`, { headers, responseType: 'blob' });
    }
    // Add this to the admin.service.ts file
    updateBookingStatus(bookingId: number, status: string): Observable<any> {
        const headers = this.getAuthHeaders();
        const body = { status};
        return this.http.put(`${this.apiUrl}/admin/bookings/${bookingId}/status`, body, { headers });
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
