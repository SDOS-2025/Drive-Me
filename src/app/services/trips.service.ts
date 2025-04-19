import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface AvailableTrip {
    id: number;
    origin: string;
    destination: string;
    date: string;
    time: string;
    fare: number;
    passengerName: string;
    passengerRating: number;
    postedTime: string;
}

export interface BookingResponse {
    bookingId: number;
    pickupLocation: string;
    dropoffLocation: string;
    status: string;
    createdAt: string;
    pickupDateTime: string;
    fare: number;
    customerName: string;
    customerPhone: string;
}

@Injectable({
    providedIn: 'root'
})
export class TripsService {
    private apiUrl = 'http://localhost:8080'; // Adjust the base URL as needed

    constructor(private http: HttpClient) { }

    getAvailableTrips(): Observable<AvailableTrip[]> {
        // This endpoint should return all pending bookings without an assigned driver
        const header = this.getAuthHeaders();
        console.log("Header:", header);
        return this.http.get<BookingResponse[]>(`${this.apiUrl}/bookings/pending`, { headers: header })
            .pipe(
                map(bookings => this.mapBookingsToTrips(bookings))
            );
    }

    acceptTrip(tripId: number, driverId: number): Observable<any> {
        const header = this.getAuthHeaders();
        return this.http.put(`${this.apiUrl}/bookings/${tripId}/assign`, {}, { headers: header });
    }

    getDriverBookings(): Observable<any> {
        const header = this.getAuthHeaders();
        return this.http.get(`${this.apiUrl}/bookings/driver`, { headers: header });
    }

    completeTrip(tripId: number): Observable<any> {
        const headers = this.getAuthHeaders();
        return this.http.put(`${this.apiUrl}/bookings/${tripId}/status`, {status: "COMPLETED"}, { headers });
    }

    // Helper method to convert booking data format to trip format
    private mapBookingsToTrips(bookings: BookingResponse[]): AvailableTrip[] {
        return bookings.map(booking => {
            // Parse the creation date
            const createdAt = new Date(booking.createdAt);
            console.log("Pickup Date Time:", booking.pickupDateTime);
            const [date, time] = booking.pickupDateTime.split(' ');
            return {
                id: booking.bookingId,
                origin: booking.pickupLocation,
                destination: booking.dropoffLocation,
                date: date,
                time: time,
                fare: booking.fare,
                passengerName: booking.customerName || 'Anonymous',
                passengerRating: 4.5, // Default rating if not provided
                postedTime: this.getTimeAgo(createdAt)
            };
        });
    }

    private getTimeAgo(date: Date): string {
        const now = new Date();
        const diffMs = now.getTime() - date.getTime();
        const diffMins = Math.floor(diffMs / 60000);

        if (diffMins < 60) {
            return `${diffMins} minute${diffMins !== 1 ? 's' : ''} ago`;
        }

        const diffHours = Math.floor(diffMins / 60);
        if (diffHours < 24) {
            return `${diffHours} hour${diffHours !== 1 ? 's' : ''} ago`;
        }

        const diffDays = Math.floor(diffHours / 24);
        return `${diffDays} day${diffDays !== 1 ? 's' : ''} ago`;
    }

    getAuthHeaders(): { [key: string]: string } {
        const token = localStorage.getItem('token');
        return {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };
    }
}