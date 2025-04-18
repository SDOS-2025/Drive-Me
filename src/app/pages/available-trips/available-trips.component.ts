import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { AuthService } from '../../services/auth.service';
import { TripsService, AvailableTrip } from '../../services/trips.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-available-trips',
  standalone: true,
  imports: [
    CommonModule, 
    SidebarComponent, 
    DashboardNavbarComponent,
    MatSnackBarModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  providers: [AuthService, TripsService],
  templateUrl: './available-trips.component.html',
  styleUrl: './available-trips.component.css'
})
export class AvailableTripsComponent implements OnInit {
  driverName: string = '';
  driverId: number = 0;
  
  sidebarMenuItems = [
    { label: 'Dashboard', route: '/driver-dashboard' },
    { label: 'Available Trips', active: true, route: '/available-trips' },
    { label: 'All Trips', route: '/all-trips' },
    { label: 'Support', route: '/chat-support' },
    { label: 'Settings', route: '/settings' },
  ];

  availableTrips: AvailableTrip[] = [];
  filteredTrips: AvailableTrip[] = [];
  distanceFilter: string = 'all';
  searchQuery: string = '';
  loading: boolean = false;
  error: string | null = null;

  constructor(
    private authService: AuthService,
    private tripsService: TripsService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadDriverData();
    this.loadAvailableTrips();
  }

  loadDriverData(): void {
    const driverDetails = localStorage.getItem('currentUser');
    if (driverDetails) {
      const driver = JSON.parse(driverDetails);
      this.driverName = driver.fullName || driver.name || 'Driver';
      this.driverId = driver.id || driver.driverId || 0;
    }
  }

  loadAvailableTrips(): void {
    this.loading = true;
    this.error = null;
    
    this.tripsService.getAvailableTrips().subscribe({
      next: (trips) => {
        this.availableTrips = trips;
        this.applyFilters();
        this.loading = false;
        
        if (trips.length === 0) {
          this.showNotification('No available trips at the moment. Check back later.', 'info');
        }
      },
      error: (err) => {
        console.error('Error fetching available trips:', err);
        this.error = 'Failed to load available trips. Please try again later.';
        this.loading = false;
        this.showNotification('Failed to load available trips. Please try again later.', 'error');
      }
    });
  }

  applyFilters(): void {
    this.filteredTrips = this.availableTrips.filter(trip => {
      // Apply search query
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        return trip.origin.toLowerCase().includes(query) ||
          trip.destination.toLowerCase().includes(query) ||
          trip.passengerName.toLowerCase().includes(query);
      }
      
      return true;
    });
  }

  filterByDistance(distance: string): void {
    this.distanceFilter = distance;
    this.applyFilters();
  }

  searchTrips(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchQuery = input.value;
    this.applyFilters();
  }

  acceptTrip(tripId: number): void {
    if (!this.driverId) {
      this.showNotification('Driver information is missing. Please log in again.', 'error');
      return;
    }
    
    this.loading = true;
    this.tripsService.acceptTrip(tripId, this.driverId).subscribe({
      next: (response) => {
        console.log(`Trip ${tripId} accepted by driver ${this.driverId}`, response);
        // Remove the trip from available list
        this.availableTrips = this.availableTrips.filter(trip => trip.id !== tripId);
        this.applyFilters();
        this.loading = false;
        this.showNotification('Trip accepted successfully! The passenger will be notified.', 'success');
      },
      error: (err) => {
        console.error('Error accepting trip:', err);
        this.loading = false;
        this.showNotification(
          `Failed to accept trip: ${err.error?.message || 'Please try again later'}`, 
          'error'
        );
      }
    });
  }

  getEstimatedEarnings(fare: number): number {
    // Calculate driver's cut - typically 75-80% of the fare
    return fare * 0.8;
  }

  // Retry loading trips if there was an error
  retryLoad(): void {
    this.loadAvailableTrips();
  }

  // Show notification using MatSnackBar
  showNotification(message: string, type: 'success' | 'error' | 'info'): void {
    const duration = type === 'error' ? 8000 : 5000;
    
    this.snackBar.open(message, 'Close', {
      duration: duration,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: ['notification', `notification-${type}`]
    });
  }
}