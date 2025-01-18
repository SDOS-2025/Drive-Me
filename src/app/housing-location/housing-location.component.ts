import { Component, Input } from '@angular/core';
import { HousingLocation } from '../housing-location';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-housing-location',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="listing">
      <img class="listing-photo" [src]="housingLocation.photo" alt={{housingLocation.name}} />
      <h2 class="listing-heading">{{housingLocation.name}}</h2>
      <p class="listing-location">{{housingLocation.city}} {{housingLocation.state}}</p>
    </section>
  `,
  styleUrl: './housing-location.component.css'
})

export class HousingLocationComponent {
  @Input() housingLocation!: HousingLocation;

}
