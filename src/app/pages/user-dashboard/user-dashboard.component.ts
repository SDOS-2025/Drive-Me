import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, SidebarComponent, NavbarComponent, RouterModule],
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent implements OnInit {
  userName: string = 'Sarah';
  
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
    { icon: '🚗', title: 'Total Rides', value: '23' },
    { icon: '🎯', title: 'Upcoming Rides', value: '2' },
    { icon: '💰', title: 'Total Spent', value: '$480' },
    { icon: '⭐', title: 'Avg. Rating Given', value: '4.7' }
  ];
  
  upcomingBookings = [
    {
      icon: '🏢',
      title: 'Business Meeting',
      date: 'April 15, 2025 • 10:00 AM',
      driver: 'Michael S. • Toyota Camry',
      status: 'Confirmed',
      statusIcon: '✓',
      statusColor: '#28a745'
    },
    {
      icon: '✈️',
      title: 'Airport Pickup',
      date: 'April 20, 2025 • 8:30 PM',
      driver: 'Robert J. • Tesla Model Y',
      status: 'Pending',
      statusIcon: '⏱',
      statusColor: '#ffc107'
    }
  ];
  
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
  
  constructor() { }

  ngOnInit(): void {
  }
}