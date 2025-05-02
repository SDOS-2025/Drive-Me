import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { DriverService, DriverStatus } from '../../services/driver.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-find-driver',
  standalone: true,
  imports: [CommonModule, SidebarComponent, DashboardNavbarComponent, RouterModule, HttpClientModule, FormsModule],
  providers: [DriverService],
  templateUrl: './find-driver.component.html',
  styleUrls: ['./find-driver.component.css']
})
export class FindDriverComponent implements OnInit {
  drivers: DriverStatus[] = [];
  filteredDrivers: DriverStatus[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';
  searchTerm: string = '';
  filterStatus: string = 'all';

  sidebarMenuItems = [
    { label: 'Dashboard', route: '/user-dashboard' },
    { label: 'My Bookings', route: '/my-bookings' },
    { label: 'Find Driver', active: true },
    { label: 'My Vehicles', route: '/my-vehicles' },
    { label: 'Support', route: '/chat-support' },
    { label: 'Settings', route: '/settings' },
  ];
  
  constructor(private driverService: DriverService) {}
  
  ngOnInit(): void {
    this.loadDrivers();
  }

  loadDrivers(): void {
    console.log('Loading drivers...');
    this.isLoading = true;
    
    this.driverService.getDriverStatus().subscribe({
      next: (drivers: DriverStatus[]) => {
        this.drivers = drivers;
        this.applyFilters(); // Initialize filtered drivers
        this.isLoading = false;
        console.log('Drivers loaded successfully', drivers);
      },
      error: (error: Error) => {
        console.error('Error fetching drivers', error);
        this.errorMessage = 'Unable to load drivers. Please try again later.';
        this.isLoading = false;
      }
    });
  }
  
  applyFilters(): void {
    let filtered = this.drivers;

    
    // Apply search filter if search term exists
    if (this.searchTerm.trim()) {
      const searchLower = this.searchTerm.toLowerCase().trim();
      filtered = filtered.filter(driver => 
        driver.name.toLowerCase().includes(searchLower) ||
        driver.phone.toLowerCase().includes(searchLower)
      );
    }
    
    this.filteredDrivers = filtered;
  }
  
  onSearch(): void {
    this.applyFilters();
  }
  
  onFilterChange(): void {
    this.applyFilters();
  }
  
  bookDriver(driverId: number): void {
    // Navigate to booking form with driver ID
    // This would typically redirect to a booking form
    console.log(`Booking driver with ID: ${driverId}`);
    // Implement redirection to booking form with the driver ID
  }
  
  getRatingStars(rating: number | undefined): string {
    // Convert rating to stars (e.g., 4.5 becomes "★★★★½")
    if (rating === undefined) return 'No rating';
    const fullStars = Math.floor(rating);
    const halfStar = rating % 1 >= 0.5;
    
    let stars = '★'.repeat(fullStars);
    if (halfStar) stars += '⯪';
    
    return stars;
  }
  
  getStatusColor(status: string): string {
    switch(status) {
      case 'AVAILABLE': return '#28a745'; // Green
      case 'BUSY': return '#ffc107';      // Yellow
      case 'OFFLINE': return '#6c757d';   // Gray
      default: return '#6c757d';
    }
  }
  
  clearError(): void {
    this.errorMessage = '';
  }
}