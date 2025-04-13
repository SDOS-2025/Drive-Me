import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { UserBookingService, BookingSummary } from '../../services/user.service';
import { DriverService } from '../../services/driver.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, SidebarComponent, DashboardNavbarComponent, RouterModule, HttpClientModule],
  providers: [UserBookingService, DriverService],
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent implements OnInit {
  userName: string = 'Sarah';
  isLoading: boolean = false;
  errorMessage: string = '';
  
  // All bookings from the backend
  allBookings: BookingSummary[] = [];
  
  // Upcoming/Active bookings (PENDING or CONFIRMED)
  upcomingBookings: BookingSummary[] = [];
  
  // Past bookings (COMPLETED or CANCELLED)
  pastBookings: BookingSummary[] = [];
  
  // Tabs for switching between booking views
  activeTab: 'upcoming' | 'past' | 'all' = 'upcoming';

  sidebarMenuItems = [
    { label: 'Dashboard', active: true },
    { label: 'My Bookings' },
    { label: 'Find Driver' },
    { label: 'My Vehicles' , route: '/my-vehicles'},
    { label: 'Support' },
    { label: 'Settings' },
    { label: 'My Profile' },
  ];
  
  stats = [
    { icon: '🚗', title: 'Total Rides', value: '0' },
    { icon: '🎯', title: 'Upcoming Rides', value: '0' },
    { icon: '💰', title: 'Total Spent', value: '$0' },
  ];
  
  // Keep original sample data for top drivers and activities
  topDrivers = [ {name: 'John Doe', number: '1234567890', icon: "👤"} ];
  
  recentActivities = [
    {
      icon: '💰',
      title: 'Payment Completed',
      details: 'April 5, 2025 • $45.00'
    },
    {
      icon: '⭐',
      title: 'Rated Robert J.',
      details: 'April 2, 2025 • ⭐⭐⭐⭐⭐'
    },
    {
      icon: '🚗',
      title: 'Completed Trip',
      details: 'March 30, 2025 • Downtown'
    }
  ];  
  constructor(
    private userBookingService: UserBookingService,
    private authService: AuthService,
    private driverService: DriverService
  ) {}
  
  ngOnInit(): void {
    this.loadUserData();
    this.loadBookings();
    this.loadDrivers();
  }

  loadDrivers(): void {
    console.log('Loading drivers...');
    this.isLoading = true;
    this.driverService.getDriverList().subscribe({
      next: (drivers: any[]) => {
        console.log('Drivers loaded successfully', drivers);
        this.topDrivers = drivers.map(driver => ({
          name: driver.fullName,
          number: driver.phone,
          icon: "👤"
        }));
        this.isLoading = false;
      },
      error: (error: Error) => {
        console.error('Error fetching drivers', error);
        this.errorMessage = 'Unable to load drivers. Please try again later.';
        this.isLoading = false;
      }
    });
  }
  
  loadUserData(): void {
    console.log('Loading user data');
    const user = this.authService.currentUserValue;
    if (user && user.fullName) {
      this.userName = user.fullName.split(' ')[0];
    }
    console.log('User name set to:', this.userName);
  } 

  loadBookings(): void {
    console.log('Loading bookings...');
    this.isLoading = true;
    this.userBookingService.getUserBookings().subscribe({
      next: (bookings: any[]) => {
        this.allBookings = bookings;
        
        console.log('All bookings:', this.allBookings);
        // Filter upcoming bookings (PENDING or CONFIRMED)
        this.upcomingBookings = bookings.filter((booking: { status: string; }) => 
          booking.status === 'PENDING' || booking.status === 'CONFIRMED'
        );
        
        // Filter past bookings (COMPLETED or CANCELLED)
        this.pastBookings = bookings.filter((booking: { status: string; }) => 
          booking.status === 'COMPLETED' || booking.status === 'CANCELLED'
        );
        
        // Update stats
        this.updateStats(bookings);
        this.isLoading = false;
        console.log('Bookings loaded successfully', bookings);
      },
      error: (error: Error) => {
        console.error('Error fetching bookings', error);
        this.errorMessage = 'Unable to load your bookings. Please try again later.';
        this.isLoading = false;
      }
    });
  } 
  filterBookings(): void {
    this.upcomingBookings = this.allBookings.filter(
      booking => booking.status === 'PENDING' || booking.status === 'CONFIRMED'
    );
    
    this.pastBookings = this.allBookings.filter(
      booking => booking.status === 'COMPLETED' || booking.status === 'CANCELLED'
    );
  }
  
  // Method to clear error messages
  clearError(): void {
    this.errorMessage = '';
  }
  
  updateStats(bookings: BookingSummary[]): void {
    // Update total rides
    this.stats[0].value = bookings.length.toString();
    
    // Update upcoming rides
    this.stats[1].value = this.upcomingBookings.length.toString();
    
    // Calculate total spent
    const totalSpent = bookings
      .filter(booking => booking.status === 'COMPLETED')
      .reduce((sum, booking) => sum + booking.fare, 0);
    this.stats[2].value = `$${totalSpent}`;
  }
  
  setActiveTab(tab: 'upcoming' | 'past' | 'all'): void {
    console.log('Setting active tab to:', tab);
    this.activeTab = tab;
  }
  
  getStatusColor(status: string): string {
    switch(status) {
      case 'CONFIRMED': return '#28a745'; // Green
      case 'PENDING': return '#ffc107';   // Yellow
      case 'CANCELLED': return '#dc3545'; // Red
      case 'COMPLETED': return '#6c757d'; // Gray
      default: return '#6c757d';
    }
  }
  
  getStatusIcon(status: string): string {
    switch(status) {
      case 'CONFIRMED': return '✓';      // Checkmark
      case 'PENDING': return '⏱';        // Clock
      case 'CANCELLED': return '✗';      // X
      case 'COMPLETED': return '🏁';     // Flag
      default: return '❓';
    }
  }
  
  cancelBooking(bookingId: number): void {
    console.log('Attempting to cancel booking ID:', bookingId);
    if (confirm('Are you sure you want to cancel this booking?')) {
      this.isLoading = true;
      this.userBookingService.cancelBooking(bookingId).subscribe({
        next: () => {
          console.log('Booking cancelled successfully');
          // Refresh bookings
          this.loadBookings();
        },
        error: (error: any) => {
          console.error('Error cancelling booking', error);
          this.errorMessage = 'Unable to cancel booking. Please try again later.';
          this.isLoading = false;
        }
      });
    }
  }
  
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      month: 'long',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}