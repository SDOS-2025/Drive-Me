import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { AuthService } from '../../services/auth.service';
import { TripsService } from '../../services/trips.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';

interface Trip {
  id: number;
  bookingId: number;
  pickupLocation: string;
  dropOffLocation: string;
  date: string;
  time: string;
  status: string;
  fare: number;
  customerName: string;
  rating?: number;
}

@Component({
  selector: 'app-all-trips',
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
  providers: [AuthService],
  templateUrl: './all-trips.component.html',
  styleUrl: './all-trips.component.css'
})
export class AllTripsComponent implements OnInit {
  driverName: string = '';
  driverId: number = 0;
  
  sidebarMenuItems = [
    { label: 'Dashboard', route: '/driver-dashboard' },
    { label: 'Available Trips', route: '/available-trips' },
    { label: 'All Trips', active: true, route: '/all-trips' },
    { label: 'Support', route: '/chat-support' },
    { label: 'Settings', route: '/settings' },
  ];

  allTrips: Trip[] = [];
  filteredTrips: Trip[] = [];
  statusFilter: string = 'all';
  searchQuery: string = '';
  loading: boolean = false;
  processingTrip: number | null = null;

  constructor(
    private authService: AuthService, 
    private tripsService: TripsService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadDriverData();
    this.loadTrips();
  }

  loadDriverData(): void {
    const driverDetails = localStorage.getItem('currentUser');
    if (driverDetails) {
      const driver = JSON.parse(driverDetails);
      this.driverName = driver.fullName || 'John Doe';
      this.driverId = driver.id || 1;
    }
  }

  loadTrips(): void {
    this.loading = true;
    this.tripsService.getDriverBookings().subscribe({
      next: (response) => {
        console.log('Trip response:', response);
        this.allTrips = response.map((trip: any) => {
          // Parse date and time from pickupDateTime if it exists
          let tripDate = '';
          let tripTime = '';
          
          if (trip.pickupDateTime) {
            const [date, time] = trip.pickupDateTime.split(' ');
            tripDate = date;
            tripTime = time;
          }
          
          return {
            ...trip,
            id: trip.bookingId, // For compatibility with existing code
            bookingId: trip.bookingId,
            date: tripDate,
            time: tripTime,
            status: trip.status || 'CONFIRMED',
            customerName: trip.customerName || 'Anonymous',
          };
        });
        
        this.filteredTrips = [...this.allTrips]; // Initialize filtered trips
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error('Error loading trips:', err);
        this.showNotification('Failed to load trips. Please try again later.', 'error');
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredTrips = this.allTrips.filter(trip => {
      // Apply status filter
      if (this.statusFilter !== 'all' && trip.status !== this.statusFilter) {
        return false;
      }
      
      // Apply search query
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        return trip.pickupLocation.toLowerCase().includes(query) ||
          trip.dropOffLocation.toLowerCase().includes(query) ||
          trip.customerName.toLowerCase().includes(query);
      }
      
      return true;
    });
  }

  filterByStatus(status: string): void {
    this.statusFilter = status;
    this.applyFilters();
  }

  searchTrips(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchQuery = input.value;
    this.applyFilters();
  }

  getStatusClass(status: string): string {
    switch (status.toUpperCase()) {
      case 'COMPLETED': return 'status-completed';
      case 'CONFIRMED': return 'status-upcoming';
      case 'CANCELLED': return 'status-canceled';
      default: return 'status-upcoming';
    }
  }
  
  formatStatus(status: string): string {
    switch (status.toUpperCase()) {
      case 'COMPLETED': return 'Completed';
      case 'CONFIRMED': return 'Upcoming';
      case 'CANCELLED': return 'Cancelled';
      default: return status;
    }
  }
  
  // New method to complete a trip
  completeTrip(trip: Trip): void {
    if (trip.status === 'COMPLETED') {
      this.showNotification('This trip is already completed.', 'info');
      return;
    }
    
    if (trip.status === 'CANCELLED') {
      this.showNotification('Cannot complete a cancelled trip.', 'error');
      return;
    }
    
    // Set the processing state for this trip
    this.processingTrip = trip.bookingId;
    
    this.tripsService.completeTrip(trip.bookingId).subscribe({
      next: () => {
        // Update the trip status locally
        trip.status = 'COMPLETED';
        
        // Update the filtered trips as well
        this.applyFilters();
        
        this.showNotification('Trip completed successfully!', 'success');
        this.processingTrip = null;
      },
      error: (err) => {
        console.error('Error completing trip:', err);
        this.showNotification(
          `Failed to complete trip: ${err.error?.message || 'Please try again later'}`, 
          'error'
        );
        this.processingTrip = null;
      }
    });
  }
  
  // Show notifications with MatSnackBar
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