import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { UserBookingService, BookingSummary } from '../../services/user.service';
import { DriverService } from '../../services/driver.service';
import { AuthService } from '../../services/auth.service';

interface Activity {
  title: string;
  details: string;
  date?: Date; // For sorting
  bookingId?: number; // For reference
}

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
    { label: 'My Bookings', route: '/my-bookings' },
    { label: 'Find Driver', route: '/find-driver' },
    { label: 'My Vehicles', route: '/my-vehicles'},
    { label: 'Support', route: '/chat-support' },
    { label: 'Settings', route: '/settings' },
  ];
  
  stats = [
    { title: 'Total Rides', value: '0' },
    { title: 'Upcoming Rides', value: '0' },
    { title: 'Total Spent', value: '₹0' },
  ];
  
  // Keep original sample data for top drivers and activities
  topDrivers = [ {name: 'John Doe', number: '1234567890', icon: "👤"} ];
  
  recentActivities: Activity[] = []
  constructor(
    private userBookingService: UserBookingService,
    private authService: AuthService,
    private driverService: DriverService
  ) {}
  
  ngOnInit(): void {
    this.loadUserData();
    this.loadBookings();
    this.loadDrivers();
    this.loadStats();
  }

  loadDrivers(): void {
    this.isLoading = true;
    this.driverService.getDriverList().subscribe({
      next: (drivers: any[]) => {
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
    const user = this.authService.currentUserValue;
    if (user && user.fullName) {
      this.userName = user.fullName.split(' ')[0];
    }
  } 

  loadBookings(): void {
    this.isLoading = true;
    this.userBookingService.getUserBookings().subscribe({
      next: (bookings: any[]) => {
        this.allBookings = bookings;

        if (!bookings || bookings.length === 0) {
          this.errorMessage = 'No bookings found.';
          this.isLoading = false;
          return;
        }

        // Convert bookings to activities
        const activities = this.convertBookingsToActivities(bookings);

        // Sort activities by date
        this.recentActivities = activities.sort((a, b) => {
          return new Date(b.date!).getTime() - new Date(a.date!).getTime();
        }).slice(0, 3); // Limit to 5 recent activities
        
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

  async loadStats() {
    try {
      const [all, confirmed, completed] = await Promise.all([
        this.userBookingService.getUserBookings().toPromise(),
        this.userBookingService.getConfirmedBookings().toPromise(),
        this.userBookingService.getCompletedBookings().toPromise()
      ]);
      
      // Update stats with real data
      if (all) {
        this.stats[0].value = all.length.toString();
      }
      
      if (confirmed) {
        this.stats[1].value = confirmed.length.toString();
      }
      
      if (completed) {
        // Calculate total spent from completed bookings
        const totalSpent = completed.reduce((sum: any, booking: { fare: any; }) => sum + booking.fare, 0);
        this.stats[2].value = `$${totalSpent.toFixed(2)}`;
      }
    } catch (error) {
      console.error('Error loading stats:', error);
    }
  }
  
  convertBookingsToActivities(bookings: BookingSummary[]): Activity[] {
    return bookings.map(booking => {
      let activity: Activity;
      const bookingDate = booking.createdAt ? new Date(booking.createdAt) : new Date();
      const formattedDate = bookingDate.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
      });
      
      switch (booking.status) {
        case 'COMPLETED':
          activity = {
            title: 'Completed Trip',
            details: `${formattedDate} • ${booking.pickupLocation} to ${booking.dropoffLocation}`,
            date: bookingDate,
            bookingId: booking.bookingId
          };
          break;
        
        case 'CONFIRMED':
          activity = {
            title: 'Upcoming Trip',
            details: `${formattedDate} • ${booking.pickupLocation}`,
            date: bookingDate,
            bookingId: booking.bookingId
          };
          break;
          
        case 'CANCELLED':
          activity = {
            title: 'Cancelled Booking',
            details: `${formattedDate} • ${booking.pickupLocation}`,
            date: bookingDate,
            bookingId: booking.bookingId
          };
          break;
          
        case 'PAID':
          activity = {
            title: 'Payment Completed',
            details: `${formattedDate} • $${booking.fare.toFixed(2)}`,
            date: bookingDate,
            bookingId: booking.bookingId
          };
          break;
          
        default:
          activity = {
            title: 'Booking Created',
            details: `${formattedDate} • ${booking.pickupLocation}`,
            date: bookingDate,
            bookingId: booking.bookingId
          };
      }
      
      return activity;
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