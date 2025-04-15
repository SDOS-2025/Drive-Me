import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { AuthService } from '../../services/auth.service';

interface AvailableTrip {
  id: number;
  icon: string;
  origin: string;
  destination: string;
  date: string;
  time: string;
  estimatedDistance: string;
  estimatedDuration: string;
  fare: number;
  passengerName: string;
  passengerRating: number;
  postedTime: string;
}

@Component({
  selector: 'app-available-trips',
  standalone: true,
  imports: [CommonModule, SidebarComponent, DashboardNavbarComponent],
  providers: [AuthService],
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
    { label: 'Notifications' },
    { label: 'Chat Support' },
    { label: 'Settings' },
    { label: 'My Profile' },
  ];

  availableTrips: AvailableTrip[] = [];
  filteredTrips: AvailableTrip[] = [];
  distanceFilter: string = 'all';
  searchQuery: string = '';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.loadDriverData();
    this.loadAvailableTrips();
  }

  loadDriverData(): void {
    const driverDetails = localStorage.getItem('currentUser');
    if (driverDetails) {
      const driver = JSON.parse(driverDetails);
      this.driverName = driver.fullName || 'John Doe';
      this.driverId = driver.id || 1;
    }
  }

  loadAvailableTrips(): void {
    // In a real app, this would come from an API service
    this.availableTrips = [
      {
        id: 1,
        icon: '🏙️',
        origin: 'Downtown Metro Station',
        destination: 'International Airport',
        date: '2025-04-16',
        time: '13:30',
        estimatedDistance: '18.5 km',
        estimatedDuration: '25 min',
        fare: 38.50,
        passengerName: 'Alex Johnson',
        passengerRating: 4.8,
        postedTime: '5 min ago'
      },
      {
        id: 2,
        icon: '🏫',
        origin: 'University Campus',
        destination: 'Central Library',
        date: '2025-04-16',
        time: '15:00',
        estimatedDistance: '5.2 km',
        estimatedDuration: '12 min',
        fare: 15.25,
        passengerName: 'Sophia Wang',
        passengerRating: 4.6,
        postedTime: '12 min ago'
      },
      {
        id: 3,
        icon: '🏥',
        origin: 'General Hospital',
        destination: 'Golden Gate Apartments',
        date: '2025-04-16',
        time: '16:15',
        estimatedDistance: '9.8 km',
        estimatedDuration: '18 min',
        fare: 22.40,
        passengerName: 'Robert Chen',
        passengerRating: 4.9,
        postedTime: '23 min ago'
      },
      {
        id: 4,
        icon: '🛒',
        origin: 'Grand Shopping Mall',
        destination: 'Riverside Homes',
        date: '2025-04-16',
        time: '18:45',
        estimatedDistance: '12.3 km',
        estimatedDuration: '22 min',
        fare: 28.70,
        passengerName: 'Emma Davis',
        passengerRating: 4.7,
        postedTime: '30 min ago'
      },
      {
        id: 5,
        icon: '🏢',
        origin: 'Business District Tower',
        destination: 'Suburban Heights',
        date: '2025-04-17',
        time: '08:30',
        estimatedDistance: '22.6 km',
        estimatedDuration: '35 min',
        fare: 45.90,
        passengerName: 'James Wilson',
        passengerRating: 4.5,
        postedTime: '1 hour ago'
      }
    ];
    
    this.applyFilters();
  }

  applyFilters(): void {
    this.filteredTrips = this.availableTrips.filter(trip => {
      // Apply distance filter
      if (this.distanceFilter !== 'all') {
        const distance = parseFloat(trip.estimatedDistance.split(' ')[0]);
        
        if (this.distanceFilter === 'short' && distance >= 10) {
          return false;
        }
        
        if (this.distanceFilter === 'medium' && (distance < 10 || distance >= 20)) {
          return false;
        }
        
        if (this.distanceFilter === 'long' && distance < 20) {
          return false;
        }
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
    // In a real app, this would call an API to accept the trip
    console.log(`Trip ${tripId} accepted by driver ${this.driverId}`);
    // After accepting, you would typically reload trips or navigate
    
    // For demo, remove the trip from available list
    this.availableTrips = this.availableTrips.filter(trip => trip.id !== tripId);
    this.applyFilters();
    
    // Show temporary success message - you'd use a proper notification service in a real app
    alert('Trip accepted successfully! The passenger will be notified.');
  }

  getEstimatedEarnings(fare: number): number {
    // Calculate driver's cut - typically 75-80% of the fare
    return fare * 0.8;
  }
}