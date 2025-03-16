import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

interface NavLink {
  path: string;
  label: string;
}

@Component({
  selector: 'app-navbar',
  imports: [RouterLink],
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