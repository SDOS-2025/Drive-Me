import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { AuthService } from '../../services/auth.service';

interface Trip {
  id: number;
  icon: string;
  origin: string;
  destination: string;
  date: string;
  time: string;
  status: string;
  fare: number;
  passengerName: string;
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
    { label: 'Dashboard' },
    { label: 'Bookings' },
    { label: 'Available Trips' },
    { label: 'All Trips', active: true },
    { label: 'Notifications' },
    { label: 'Chat Support' },
    { label: 'Settings' },
    { label: 'My Profile' },
  ];

  allTrips: Trip[] = [];
  filteredTrips: Trip[] = [];
  statusFilter: string = 'all';
  searchQuery: string = '';

  constructor(private authService: AuthService) {}

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
    this.allTrips = [
      {
        id: 1,
        icon: '🏙️',
        origin: 'Downtown',
        destination: 'Airport',
        date: '2025-04-10',
        time: '09:30 AM',
        status: 'completed',
        fare: 35.50,
        passengerName: 'Emily Johnson',
        rating: 4.8
      },
      {
        id: 2,
        icon: '🏫',
        origin: 'University Campus',
        destination: 'Shopping Mall',
        date: '2025-04-12',
        time: '02:15 PM',
        status: 'completed',
        fare: 22.75,
        passengerName: 'Michael Chen',
        rating: 4.5
      },
      {
        id: 3,
        icon: '🏥',
        origin: 'Hospital',
        destination: 'Residential Area',
        date: '2025-04-13',
        time: '05:45 PM',
        status: 'canceled',
        fare: 18.20,
        passengerName: 'Sarah Williams',
      },
      {
        id: 4,
        icon: '🏨',
        origin: 'Hotel Grand',
        destination: 'Conference Center',
        date: '2025-04-14',
        time: '10:00 AM',
        status: 'upcoming',
        fare: 15.80,
        passengerName: 'Robert Davis',
      },
      {
        id: 5,
        icon: '🛒',
        origin: 'Grocery Store',
        destination: 'Apartment Complex',
        date: '2025-04-15',
        time: '06:30 PM',
        status: 'upcoming',
        fare: 12.40,
        passengerName: 'Jennifer Lopez',
      }
    ];
    
    this.applyFilters();
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
        return trip.origin.toLowerCase().includes(query) ||
          trip.destination.toLowerCase().includes(query) ||
          trip.passengerName.toLowerCase().includes(query);
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
    switch (status) {
      case 'completed': return 'status-completed';
      case 'upcoming': return 'status-upcoming';
      case 'canceled': return 'status-canceled';
      default: return '';
    }
  }
}