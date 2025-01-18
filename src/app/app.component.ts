import { Component } from '@angular/core';
import { HomeComponent } from './home/home.component';

@Component({
  selector: 'app-root',
  // templateUrl: './app.component.html',
  template: `
    <main>
      <header class="brand-name">
        <img class="brand-logo" src="assets/logo.svg" alt="logo" />
      </header>
      <section class="content">
        <app-home></app-home>
      </section>
    </main>
  `,
  styleUrl: './app.component.css',
  imports: [HomeComponent]
})
export class AppComponent {
  title = 'driveme';
}
