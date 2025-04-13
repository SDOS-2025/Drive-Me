import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service'; // Assuming you have an auth service

interface NavLink {
  path: string;
  label: string;
}

@Component({
  selector: 'app-dashboard-navbar',
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard-navbar.component.html',
  styleUrl: './dashboard-navbar.component.css',
  standalone: true
})
export class DashboardNavbarComponent implements OnInit {
  navLinks: NavLink[] = [];
  isDriver: boolean = false;
  homeLink: string = '/user-dashboard';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.currentUserValue && this.authService.currentUserValue.role === 'driver') {
      this.isDriver = true;
    }
    this.homeLink = this.isDriver ? '/driver-dashboard' : '/user-dashboard';
    
    this.setupNavLinks();
  }

  setupNavLinks(): void {
    this.navLinks = [
      { path: this.homeLink, label: 'Home' },
      // Add other common navigation links here
    ];
    
    // Add driver-specific links if the user is a driver
    if (this.isDriver) {
      this.navLinks.push(
        { path: '/rides', label: 'My Rides' },
        // Other driver-specific links
      );
    } else {
      this.navLinks.push(
        { path: '/driver-booking', label: 'Book a Ride' },
        // Other regular user-specific links
      );
    }
  }

  navigateHome(): void {
    this.router.navigate([this.homeLink]);
  }

  logout(): void {
    console.log('Logging out...');
    this.authService.logout(); // Assuming you have a logout method in your auth service
  }
}