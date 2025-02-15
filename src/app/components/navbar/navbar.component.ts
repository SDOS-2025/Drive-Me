import { Component } from '@angular/core';
import { Router } from '@angular/router';

interface NavLink {
  path: string;
  label: string;
}

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  links: NavLink[] = [
    { path: '/', label: 'Home' },
    { path: '/book', label: 'Book a Driver' },
    { path: '/contact', label: 'Contact Us' }
  ];

  constructor(private router: Router) {}

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }

  isActive(path: string): boolean {
    return this.router.url === path;
  }
}