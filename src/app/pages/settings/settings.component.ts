import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, DashboardNavbarComponent],
  providers: [AuthService],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent implements OnInit {
  userType: string = 'user'; // 'user' or 'driver'
  userId: number = 0;
  userName: string = '';
  userEmail: string = '';
  
  // Settings categories
  activeTab: string = 'account';
  
  // User settings
  notificationSettings = {
    emailNotifications: true,
    pushNotifications: true,
    smsNotifications: false,
    tripUpdates: true,
    promotionalOffers: false,
    securityAlerts: true
  };
  
  privacySettings = {
    shareLocationData: true,
    shareProfileWithDrivers: true,
    allowReviewsAndRatings: true,
    shareRideHistory: false
  };
  
  // Driver-specific settings
  driverSettings = {
    automaticallyAcceptTrips: false,
    maxTripDistance: 25,
    preferredTripTypes: ['standard', 'premium'],
    availableHours: {
      start: '08:00',
      end: '18:00'
    },
    breakTime: {
      enabled: true,
      start: '12:00',
      duration: 60 // minutes
    }
  };
  
  // Account settings
  accountSettings = {
    language: 'english',
    currency: 'usd',
    darkMode: false
  };
  
  // Languages and currencies for selection
  availableLanguages = [
    { value: 'english', label: 'English' },
    { value: 'spanish', label: 'Spanish' },
    { value: 'french', label: 'French' },
    { value: 'german', label: 'German' },
    { value: 'chinese', label: 'Chinese' },
    { value: 'japanese', label: 'Japanese' },
    { value: 'arabic', label: 'Arabic' },
    { value: 'hindi', label: 'Hindi' }
  ];
  
  availableCurrencies = [
    { value: 'usd', label: 'US Dollar (USD)' },
    { value: 'eur', label: 'Euro (EUR)' },
    { value: 'gbp', label: 'British Pound (GBP)' },
    { value: 'jpy', label: 'Japanese Yen (JPY)' },
    { value: 'cad', label: 'Canadian Dollar (CAD)' },
    { value: 'aud', label: 'Australian Dollar (AUD)' },
    { value: 'inr', label: 'Indian Rupee (INR)' },
    { value: 'cny', label: 'Chinese Yuan (CNY)' }
  ];
  
  // Trip types for drivers
  availableTripTypes = [
    { value: 'standard', label: 'Standard' },
    { value: 'premium', label: 'Premium' },
    { value: 'express', label: 'Express' },
    { value: 'group', label: 'Group/Shared' },
    { value: 'special', label: 'Special Assistance' }
  ];
  
  // Sidebar menu items - will be set based on user type
  sidebarMenuItems: any[] = [];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.setupSidebarMenu();
  }

  loadUserData(): void {
    const userData = localStorage.getItem('currentUser');
    if (userData) {
      const user = JSON.parse(userData);
      this.userName = user.fullName || 'User';
      this.userId = user.id || 0;
      this.userEmail = user.email || 'user@example.com';
      
      // Determine user type
      if (user.role) {
        this.userType = user.role.toLowerCase();
      }
    }
  }

  setupSidebarMenu(): void {
    if (this.userType === 'driver') {
      this.sidebarMenuItems = [
        { label: 'Dashboard', route: '/driver-dashboard' },
        { label: 'Available Trips', route: '/available-trips' },
        { label: 'All Trips', route: '/all-trips' },
        { label: 'Chat Support', route: '/chat-support' },
        { label: 'Settings', active: true, route: '/settings' },
      ];
    } else {
      this.sidebarMenuItems = [
        { label: 'Dashboard', route: '/user-dashboard' },
        { label: 'Book a Ride', route: '/find-driver' },
        { label: 'My Bookings', route: '/my-bookings' },
        { label: 'My Vehicles', route: '/my-vehicles' },
        { label: 'Chat Support', route: '/chat-support' },
        { label: 'Settings', active: true, route: '/settings' },
      ];
    }
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  saveSettings(): void {
    // In a real app, you would send these settings to a backend service
    console.log('Saving settings...');
    
    const settings = {
      notification: this.notificationSettings,
      privacy: this.privacySettings,
      account: this.accountSettings
    };
    
    if (this.userType === 'driver') {
      Object.assign(settings, { driver: this.driverSettings });
    }
    
    // Simulate saving
    setTimeout(() => {
      alert('Settings saved successfully!');
    }, 1000);
  }

  toggleTripType(type: string): void {
    const index = this.driverSettings.preferredTripTypes.indexOf(type);
    if (index === -1) {
      this.driverSettings.preferredTripTypes.push(type);
    } else {
      this.driverSettings.preferredTripTypes.splice(index, 1);
    }
  }

  isTripTypeSelected(type: string): boolean {
    return this.driverSettings.preferredTripTypes.includes(type);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  deleteAccount(): void {
    const confirmed = confirm('Are you sure you want to delete your account? This action cannot be undone.');
    if (confirmed) {
      // In a real app, call the service to delete the account
      console.log('Deleting account...');
      setTimeout(() => {
        alert('Your account has been deleted successfully.');
        this.authService.logout();
        this.router.navigate(['/']);
      }, 1500);
    }
  }
}