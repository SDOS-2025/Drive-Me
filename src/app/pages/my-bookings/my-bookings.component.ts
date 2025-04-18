import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { UserBookingService, BookingSummary } from '../../services/user.service';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-my-bookings',
  standalone: true,
  imports: [CommonModule, SidebarComponent, DashboardNavbarComponent, RouterModule, HttpClientModule],
  providers: [UserBookingService],
  templateUrl: './my-bookings.component.html',
  styleUrls: ['./my-bookings.component.css']
})
export class MyBookingsComponent implements OnInit {
  isLoading: boolean = false;
  errorMessage: string = '';

  // All bookings from the backend
  allBookings: BookingSummary[] = [];
  
  // Upcoming/Active bookings (PENDING or CONFIRMED)
  upcomingBookings: BookingSummary[] = [];
  
  // Past bookings (COMPLETED or CANCELLED)
  pastBookings: BookingSummary[] = [];
  cancelledBookings: BookingSummary[] = [];
  completedBookings: BookingSummary[] = [];
  
  // Tabs for switching between booking views
  activeTab: 'upcoming' | 'past' | 'cancelled' | 'completed' | 'all' = 'upcoming';

  sidebarMenuItems = [
    { label: 'Dashboard', route: '/user-dashboard' },
    { label: 'My Bookings', active: true },
    { label: 'Find Driver', route: '/find-driver' },
    { label: 'My Vehicles', route: '/my-vehicles' },
    { label: 'Support' },
    { label: 'Settings', route: '/user-settings' },
  ];
  
  constructor(private userBookingService: UserBookingService) {}
  
  ngOnInit(): void {
    this.loadBookings();
  }

  loadBookings(): void {
    console.log('Loading bookings...');
    this.isLoading = true;
    this.userBookingService.getUserBookings().subscribe({
      next: (bookings: BookingSummary[]) => {
        this.allBookings = bookings;
        
        // Filter upcoming bookings (PENDING or CONFIRMED)
        this.upcomingBookings = bookings.filter(booking => 
          booking.status === 'PENDING' || booking.status === 'CONFIRMED'
        );
        
        // Filter completed bookings
        this.completedBookings = bookings.filter(booking => 
          booking.status === 'COMPLETED'
        );
        
        // Filter cancelled bookings
        this.cancelledBookings = bookings.filter(booking => 
          booking.status === 'CANCELLED'
        );
        
        // Filter past bookings (COMPLETED or CANCELLED)
        this.pastBookings = bookings.filter(booking => 
          booking.status === 'COMPLETED' || booking.status === 'CANCELLED'
        );
        
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
  
  setActiveTab(tab: 'upcoming' | 'past' | 'cancelled' | 'completed' | 'all'): void {
    console.log('Setting active tab to:', tab);
    this.activeTab = tab;
  }
  
  cancelBooking(bookingId: number): void {
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
  
  clearError(): void {
    this.errorMessage = '';
  }
}