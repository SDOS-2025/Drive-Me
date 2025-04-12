import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { UserBookingService, BookingSummary } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, SidebarComponent, NavbarComponent, RouterModule, HttpClientModule],
  providers: [UserBookingService],
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
    { icon: '📊', label: 'Dashboard', active: true },
    { icon: '📅', label: 'My Bookings' },
    { icon: '🚗', label: 'Find Driver' },
    { icon: '⭐', label: 'My Ratings' },
    { icon: '🏆', label: 'Top Drivers' },
    { icon: '💰', label: 'Payment History' },
    { icon: '👤', label: 'My Profile' },
    { icon: '⚙️', label: 'Settings' },
    { icon: '💬', label: 'Support' },
    { icon: '🚪', label: 'Sign Out' }
  ];
  
  stats = [
    { icon: '🚗', title: 'Total Rides', value: '0' },
    { icon: '🎯', title: 'Upcoming Rides', value: '0' },
    { icon: '💰', title: 'Total Spent', value: '$0' },
    { icon: '⭐', title: 'Avg. Rating Given', value: '0' }
  ];
  
  // Keep original sample data for top drivers and activities
  topDrivers = [
    {
      icon: '👨‍✈️',
      name: 'John Doe',
      experience: '5 years experience • 4.9 ⭐'
    },
    {
      icon: '👩‍✈️',
      name: 'Amanda G.',
      experience: '3 years experience • 4.8 ⭐'
    },
    {
      icon: '👨‍✈️',
      name: 'Michael S.',
      experience: '7 years experience • 4.7 ⭐'
    }
  ];
  
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
    private bookingService: UserBookingService,
    private authService: AuthService
  ) { 
    console.log('UserDashboardComponent constructor called');
  }

  ngOnInit(): void {
    console.log('UserDashboardComponent ngOnInit called');
    this.loadUserData();
    this.loadBookings();
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
    this.bookingService.getUserBookings().subscribe({
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
      this.bookingService.cancelBooking(bookingId).subscribe({
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