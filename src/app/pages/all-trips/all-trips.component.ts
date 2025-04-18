import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { AuthService } from '../../services/auth.service';
import { TripsService } from '../../services/trips.service';

interface Trip {
  id: number;
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
  imports: [CommonModule, SidebarComponent, DashboardNavbarComponent],
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

  constructor(private authService: AuthService, private tripsService: TripsService) {}

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
    this.tripsService.getDriverBookings().subscribe(
      (response) => {
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
            date: tripDate,
            time: tripTime,
            status: trip.status || 'CONFIRMED',
            customerName: trip.customerName || 'Anonymous',
          };
        });
        
        this.filteredTrips = [...this.allTrips]; // Initialize filtered trips
        this.applyFilters();
      },
      error => {
        console.error('Error loading trips:', error);
      }
    );
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
}