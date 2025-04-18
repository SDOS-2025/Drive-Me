import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { AuthService } from '../../services/auth.service';
import { DriverService, DriverTrip } from '../../services/driver.service';
import {  of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

interface DashboardTrip extends DriverTrip {
  title?: string;
  details?: string;
}

@Component({
  selector: 'app-driver-dashboard',
  standalone: true, 
  imports: [CommonModule, SidebarComponent, DashboardNavbarComponent],
  providers: [AuthService, DriverService],
  templateUrl: './driver-dashboard.component.html',
  styleUrls: ['./driver-dashboard.component.css']
})
export class DriverDashboardComponent implements OnInit {
  driverName: string = 'John Doe';
  driverId: number = 1;
  licenseNumber: string = 'DL1234567890';
  
  sidebarMenuItems = [
    { label: 'Dashboard', active: true, route: '/driver-dashboard' },
    { label: 'Available Trips', route: '/available-trips' },
    { label: 'All Trips', route: '/all-trips' },
    { label: 'Support', route: '/chat-support' },
    { label: 'Settings', route: '/settings' },
  ];
  
  recentTrips: DashboardTrip[] = [];
  feedbackTrips: DashboardTrip[] = [];
  
  loading: boolean = false;
  error: string | null = null;
  
  constructor(
    private driverService: DriverService,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.loadDriverData();
    this.loadDashboardData();
  }

  loadDriverData(): void {
    const driverDetails = localStorage.getItem('currentUser');
    if (driverDetails) {
      const driver = JSON.parse(driverDetails);
      this.driverName = driver.fullName || 'John Doe';
      this.driverId = driver.id || 1;
      this.licenseNumber = driver.licenseNumber || 'DL1234567890';
    }
  }
  
  loadDashboardData(): void {
    this.loading = true;
    this.error = null;
    
    // Get driver bookings
    this.driverService.getDriverBookings()
      .pipe(
        catchError(err => {
          console.error('Error fetching driver bookings:', err);
          this.error = 'Failed to load dashboard data. Please try again.';
          return of([]);
        })
      )
      .subscribe(bookings => {
        this.loading = false;
        
        if (bookings.length === 0) {
          // No bookings found
          return;
        }
        
        // Sort bookings by created date (newest first)
        const sortedBookings = [...bookings].sort((a, b) => {
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        });
        
        // Get recent trips (last 2)
        this.recentTrips = sortedBookings.slice(0, 2).map(booking => {
          return {
            ...booking,
            title: `Trip to ${booking.dropoffLocation}`,
            details: `From ${booking.pickupLocation}`
          };
        });
        
        // Get completed trips with feedback (assuming feedback would be in a property)
        this.feedbackTrips = sortedBookings
          .filter(booking => booking.status === 'COMPLETED')
          .slice(0, 2)
          .map(booking => {
            return {
              ...booking,
              title: `Trip to ${booking.dropoffLocation}`,
              details: booking.customerName ? `From ${booking.customerName}` : 'Anonymous'
            };
          });
        
        // If no feedback trips, use default
        if (this.feedbackTrips.length === 0) {
          this.feedbackTrips = [
            {
              bookingId: 0,
              pickupLocation: '',
              dropoffLocation: '',
              status: 'COMPLETED',
              createdAt: '',
              fare: 0,
              title: 'No feedback yet',
              details: 'Complete more trips to get customer feedback'
            }
          ];
        }
      });
  }
  navigateToAllTrips(): void {
  this.router.navigate(['/all-trips']);
}
}