import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { UserBookingService, BookingSummary } from '../../services/user.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-my-bookings',
  standalone: true,
  imports: [CommonModule, SidebarComponent, DashboardNavbarComponent, RouterModule, HttpClientModule, FormsModule],
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

  // Review Modal
  showReviewModal: boolean = false;
  selectedBooking: BookingSummary | null = null;
  rating: number = 0;
  reviewComment: string = '';
  reviewSubmitted: boolean = false;
  reviewError: string = '';
  isSubmittingReview: boolean = false;


  sidebarMenuItems = [
    { label: 'Dashboard', route: '/user-dashboard' },
    { label: 'My Bookings', active: true },
    { label: 'Find Driver', route: '/find-driver' },
    { label: 'My Vehicles', route: '/my-vehicles' },
    { label: 'Support', route: '/chat-support' },
    { label: 'Settings', route: '/user-settings' },
  ];
  
  constructor(
    private userBookingService: UserBookingService,
  ) { }
  
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
    // Add these console logs to the openReviewModal method
    openReviewModal(booking: BookingSummary): void {
      console.log('openReviewModal called with booking:', booking);
      this.selectedBooking = booking;
      this.reviewSubmitted = false;
      this.rating = 0;
      this.reviewComment = '';
      this.reviewError = '';
      this.showReviewModal = true;
      console.log('showReviewModal set to:', this.showReviewModal);
    }
  
    // Add logging to closeReviewModal
    closeReviewModal(event: Event): void {
      console.log('closeReviewModal called, event target:', (event.target as HTMLElement).className);
      // Only close if clicking on backdrop or close button
      if (
        (event.target as HTMLElement).className === 'review-modal-backdrop' ||
        (event.target as HTMLElement).className === 'review-modal-close'
      ) {
        this.showReviewModal = false;
        this.selectedBooking = null;
        console.log('Modal closed, showReviewModal set to:', this.showReviewModal);
      }
    }
  
    // Add logging to setRating
    setRating(value: number): void {
      console.log('setRating called with value:', value);
      this.rating = value;
    }
  
    // Add logging to submitReview
    submitReview(): void {
      console.log('submitReview called, rating:', this.rating, 'comment:', this.reviewComment);
  
      if (this.rating === 0) {
        this.reviewError = 'Please select a rating before submitting';
        console.log('Rating validation failed');
        return;
      }
  
      if (!this.selectedBooking) {
        this.reviewError = 'Unable to identify booking for review';
        console.log('No booking selected');
        return;
      }
  
      this.isSubmittingReview = true;
      console.log('Submitting review...');
  
      // Simulated API call
      setTimeout(() => {
        console.log('Review submission completed');
        this.isSubmittingReview = false;
        this.reviewSubmitted = true;
  
        // After showing success message for 2 seconds, close the modal
        setTimeout(() => {
          console.log('Closing modal after successful submission');
          this.showReviewModal = false;
          this.selectedBooking = null;
        }, 2000);
      }, 1000);
    }
}