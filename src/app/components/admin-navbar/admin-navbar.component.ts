import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service'; // Assuming you have an auth service

interface NavLink {
  path: string;
  label: string;
}

@Component({
  selector: 'app-admin-navbar',
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-navbar.component.html',
  styleUrl: './admin-navbar.component.css',
  standalone: true
})
export class AdminNavbarComponent implements OnInit {
  navLinks: NavLink[] = [];
  isDriver: boolean = false;
  homeLink: string = '/user-dashboard';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    
    this.homeLink = '/admin-dashboard'; 
    
    this.setupNavLinks();
  }

  setupNavLinks(): void {
    this.navLinks = [
      { path: this.homeLink, label: 'Home' },
      // Add other common navigation links here
    ];
  }

  navigateHome(): void {
    this.router.navigate([this.homeLink]);
  }

  logout(): void {
    console.log('Logging out...');
    this.authService.logout(); // Assuming you have a logout method in your auth service
  }
}