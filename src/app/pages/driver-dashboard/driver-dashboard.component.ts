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
    this.authService.currentUser.subscribe(user => {
      if (user) {
        console.log(user);
        this.driverName = user.fullName || 'John Doe';
        this.driverId = user.id || 1;
      }
    }
  )}; 
}