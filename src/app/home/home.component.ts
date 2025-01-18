import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  imports: [],
  template: `
    <section>
      <form>
        <input type="text" placeholder="Enter your location" />
        <input type="text" placeholder="Enter your destination" />
        <button type="submit">Get Directions</button>
      </form>
    </section>
    <section class="results">
    </section>
  `,
  styleUrl: './home.component.css'
})
export class HomeComponent {

}
