import { Component } from '@angular/core';

@Component({
  selector: 'app-testimonial',
  templateUrl: './testimonial.component.html',
  styleUrls: ['./testimonial.component.css']
})
export class TestimonialComponent {
  constructor() {}

  navigateTo(section: string): void {
    console.log(`Navigating to ${section}`);
  }

}
