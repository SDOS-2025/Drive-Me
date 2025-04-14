import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-driver-dashboard',
  standalone: true, 
  imports: [CommonModule, SidebarComponent, DashboardNavbarComponent],
  providers: [AuthService],
  templateUrl: './driver-dashboard.component.html',
  styleUrls: ['./driver-dashboard.component.css']
})
export class DriverDashboardComponent implements OnInit {
  driverName: string = 'John Doe';
  driverId: number = 1;
  licenseNumber: string = 'DL1234567890';
  
  sidebarMenuItems = [
    { label: 'Dashboard', active: true },
    { label: 'Bookings' },
    { label: 'Available Trips' },
    { label: 'All Trips' },
    { label: 'Notifications' },
    { label: 'Chat Support' },
    { label: 'Settings' },
    { label: 'My Profile' },
  ];
  
  tripHistory = [
    {
      icon: '✈️',
      title: 'Airport Transfer',
      details: 'City Tour',
    },
    {
      icon: '🏢',
      title: 'Corporate Event',
      details: 'Wedding Ceremony',
    }
  ];
  
  feedbackHistory = [
    {
      icon: '💬',
      title: 'Excellent Service',
      details: 'Recommended for Future',
    },
    {
      icon: '🚨',
      title: 'Emergency Contact',
      details: 'First Aid Kit Available',
    }
  ];
  
  constructor(
    private authService: AuthService 
  ) { }

  ngOnInit(): void {
    this.loadDriverData();    
  }

  loadDriverData(): void {
    const driverDetails = localStorage.getItem('currentUser');
    if (driverDetails) {
      const driver = JSON.parse(driverDetails);
      this.driverName = driver.fullName || 'John Doe';
      this.driverId = driver.id || 1;
      this.licenseNumber = driver.licenseNumber || 'DL1234567890';
    }
    
  }; 
}