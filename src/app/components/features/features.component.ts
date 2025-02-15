import { Component } from '@angular/core';

@Component({
  selector: 'app-features',
  templateUrl: './features.component.html',
  styleUrl: './features.component.css'
})
export class FeaturesComponent {
  constructor() {}

  navigateTo(section: string): void {
    console.log(`Navigating to ${section}`);
  }

}
