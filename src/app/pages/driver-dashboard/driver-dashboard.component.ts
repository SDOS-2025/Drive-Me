import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { NavbarComponent } from "../../components/navbar/navbar.component";

@Component({
  selector: 'app-driver-dashboard',
  standalone: true, 
  imports: [CommonModule, SidebarComponent, NavbarComponent],
  templateUrl: './driver-dashboard.component.html',
  styleUrls: ['./driver-dashboard.component.css']
})
export class DriverDashboardComponent implements OnInit {
  driverName: string = 'John Doe';
  driverId: string = 'DRV123';
  
  sidebarMenuItems = [
    { icon: '📊', label: 'Bookings', active: true },
    { icon: '📅', label: 'Trips' },
    { icon: '👥', label: 'Drivers' },
    { icon: '⚙️', label: 'Manage' },
    { icon: '✅', label: 'Available' },
    { icon: '🚗', label: 'Bookings' },
    { icon: '👤', label: 'Profile' },
    { icon: '🔔', label: 'Notifications' },
    { icon: '💬', label: 'Chat Support' },
    { icon: '🚪', label: 'Sign Out' }
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
  
  stats = [
    { title: 'Rest Time', value: 'avg. 4h 45 min' },
    { title: 'Distance', value: 'avg. 100 km' },
    { title: 'Fuel Level', value: '80% full' },
    { title: 'Fuel', value: 'avg. 10 km/L' },
    { title: 'Route', value: 'Regular Route' }
  ];
  
  constructor() { }

  ngOnInit(): void {
  }
}