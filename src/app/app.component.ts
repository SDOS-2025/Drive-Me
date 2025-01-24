import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  template: `
    <main>
      <header class="navbar">
        <div class="brand">
          <img class="brand-logo" src="assets/logo.svg" alt="DriveMe logo" />
        </div>
        <nav class="nav-links">
          <a routerLink="/" routerLinkActive="active-link">Home</a>
          <a routerLink="/about" routerLinkActive="active-link">About</a>
          <a routerLink="/users" routerLinkActive="active-link">Users</a>
          <a routerLink="/drivers" routerLinkActive="active-link">Drivers</a>
          <a routerLink="/contact" routerLinkActive="active-link">Contact</a>
        </nav>
      </header>
      <section class="content">
        <router-outlet></router-outlet>
      </section>
    </main>
  `,
  styleUrls: ['./app.component.css'],
  imports: [RouterModule]
})
export class AppComponent {
  title = 'driveme';
}
