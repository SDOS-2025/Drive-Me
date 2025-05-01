import { Component, OnInit, HostListener } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { HeroComponent } from '../../components/hero/hero.component';
import { TestimonialComponent } from '../../components/testimonial/testimonial.component';
import { FooterComponent } from '../../components/footer/footer.component';
import { CommonModule } from '@angular/common';
import { trigger, transition, style, animate, state, query, stagger } from '@angular/animations';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    NavbarComponent,
    HeroComponent,
    TestimonialComponent,
    FooterComponent
  ],
  template: `
    <app-navbar></app-navbar>
    
    <app-hero></app-hero>
    
    <!-- Animated Features Section -->
    <section class="features-showcase" id="features">
      <div class="container">
        <h2 class="section-title" [@fadeIn]>Why Choose DriveMe?</h2>
        
        <div class="features-grid" [@staggerFeatures]="featuresVisible ? 'visible' : 'hidden'">
          <div class="feature-card" [@cardHover]>
            <div class="feature-icon" [@rotateIcon]>
              <img src="assets/tracking.jpg" alt="Real-time tracking">
            </div>
            <h3>Real-Time Tracking</h3>
            <p>Monitor your vehicle's location and speed in real-time with our advanced GPS technology.</p>
          </div>
          
          <div class="feature-card" [@cardHover]>
            <div class="feature-icon" [@rotateIcon]>
              <img src="assets/analytics.jpg" alt="Driver Analytics">
            </div>
            <h3>Driver Analytics</h3>
            <p>Gain valuable insights into driver behavior and improve performance with detailed metrics.</p>
          </div>
          
          <div class="feature-card" [@cardHover]>
            <div class="feature-icon" [@rotateIcon]>
              <img src="assets/alert.jpg" alt="Maintenance Alerts">
            </div>
            <h3>Maintenance Alerts</h3>
            <p>Receive timely notifications for vehicle maintenance to keep your fleet in optimal condition.</p>
          </div>
        </div>
      </div>
    </section>
    
    <!-- How It Works Section with Animation -->
    <section class="how-it-works" id="how-it-works">
      <div class="container">
        <h2 class="section-title" [@fadeIn]>How It Works</h2>
        
        <div class="steps-container" [@staggerSteps]="stepsVisible ? 'visible' : 'hidden'">
          <div class="step" [@stepHover]>
            <div class="step-number" [@countUp]="'1'">1</div>
            <div class="step-content">
              <h3>Book a Driver</h3>
              <p>Choose from our qualified pool of professional drivers</p>
              <div class="step-image-container">
                <img src="assets/book driver.jpg" alt="Book a driver" class="step-image" [@imageZoom]>
              </div>
            </div>
          </div>
          
          <div class="step" [@stepHover]>
            <div class="step-number" [@countUp]="'2'">2</div>
            <div class="step-content">
              <h3>Driver Arrives</h3>
              <p>Your driver arrives punctually at your location</p>
              <div class="step-image-container">
                <img src="assets/driver arrives.jpg" alt="Driver arrives" class="step-image" [@imageZoom]>
              </div>
            </div>
          </div>
          
          <div class="step" [@stepHover]>
            <div class="step-number" [@countUp]="'3'">3</div>
            <div class="step-content">
              <h3>Reach Destination</h3>
              <p>Safe and comfortable journey to your destination</p>
              <div class="step-image-container">
                <img src="assets/arrives.jpg" alt="Reach destination" class="step-image" [@imageZoom]>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
    
    <app-testimonial></app-testimonial>
    
    <!-- Call to Action Banner with Animation -->
    <section class="cta-banner" [@fadeInUp]>
      <div class="container">
        <div class="cta-content">
          <h2>Ready to Experience Premium Driver Service?</h2>
          <p>Join thousands of satisfied customers who trust DriveMe for their transportation needs.</p>
          <button class="btn-primary cta-button" routerLink="/login" [@pulseButton]>Get Started Now</button>
        </div>
        <div class="cta-image">
          <img src="assets/logo2.png" alt="Premium driver service" [@floatImage]>
        </div>
      </div>
    </section>
    
    <app-footer></app-footer>
  `,
  styleUrls: ['./landing.component.css'],
  animations: [
    // Existing animations
    trigger('fadeIn', [
      state('void', style({ opacity: 0 })),
      transition(':enter', [
        animate('1s ease-out', style({ opacity: 1 }))
      ])
    ]),
    trigger('fadeInUp', [
      state('void', style({ opacity: 0, transform: 'translateY(30px)' })),
      transition(':enter', [
        animate('0.8s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    
    // New staggered entrance for feature cards
    trigger('staggerFeatures', [
      state('hidden', style({})),
      state('visible', style({})),
      transition('hidden => visible', [
        query('.feature-card', [
          style({ opacity: 0, transform: 'translateY(100px)' }),
          stagger('200ms', [
            animate('0.8s cubic-bezier(0.35, 0, 0.25, 1)', 
              style({ opacity: 1, transform: 'translateY(0)' }))
          ])
        ])
      ])
    ]),
    
    // Hover animation for feature cards
    trigger('cardHover', [
      state('void', style({ transform: 'scale(1)' })),
      state('hover', style({ transform: 'scale(1.05)', boxShadow: '0 10px 30px rgba(0,0,0,0.15)' })),
      transition('void => hover', animate('200ms ease-out')),
      transition('hover => void', animate('150ms ease-in'))
    ]),
    
    // Rotating icon animation
    trigger('rotateIcon', [
      transition(':enter', [
        style({ transform: 'rotate(-30deg) scale(0.8)', opacity: 0 }),
        animate('0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275)', 
          style({ transform: 'rotate(0) scale(1)', opacity: 1 }))
      ])
    ]),
    
    // Staggered animation for steps
    trigger('staggerSteps', [
      state('hidden', style({})),
      state('visible', style({})),
      transition('hidden => visible', [
        query('.step', [
          style({ opacity: 0, transform: 'translateX(-50px)' }),
          stagger('300ms', [
            animate('0.8s cubic-bezier(0.35, 0, 0.25, 1)', 
              style({ opacity: 1, transform: 'translateX(0)' }))
          ])
        ])
      ])
    ]),
    
    // Step number count-up animation
    trigger('countUp', [
      transition(':enter', [
        style({ transform: 'scale(0)', opacity: 0 }),
        animate('0.5s 0.3s cubic-bezier(0.34, 1.56, 0.64, 1)', 
          style({ transform: 'scale(1)', opacity: 1 }))
      ])
    ]),
    
    // Step hover animation
    trigger('stepHover', [
      state('void', style({ transform: 'translateY(0)' })),
      state('hover', style({ transform: 'translateY(-10px)' })),
      transition('void => hover', animate('300ms ease-out')),
      transition('hover => void', animate('200ms ease-in'))
    ]),
    
    // Image zoom animation on hover
    trigger('imageZoom', [
      state('normal', style({ transform: 'scale(1)' })),
      state('zoomed', style({ transform: 'scale(1.1)' })),
      transition('normal <=> zoomed', animate('300ms ease-in-out'))
    ]),
    
    // Floating animation for CTA image
    trigger('floatImage', [
      state('void', style({ transform: 'translateY(0)' })),
      transition(':enter', [
        animate('3s ease-in-out infinite', 
          style({ transform: 'translateY(-15px)' })),
        animate('3s ease-in-out infinite', 
          style({ transform: 'translateY(0)' }))
      ])
    ]),
    
    // Pulsing button animation
    trigger('pulseButton', [
      transition(':enter', [
        style({ transform: 'scale(1)' }),
        animate('2s ease-in-out infinite', 
          style({ transform: 'scale(1.05)' })),
        animate('2s ease-in-out infinite', 
          style({ transform: 'scale(1)' }))
      ])
    ])
  ]
})
export class LandingComponent implements OnInit {
  featuresVisible = false;
  stepsVisible = false;
  
  constructor() {}
  
  ngOnInit() {
    // Initialize scroll detection after a short delay
    setTimeout(() => {
      this.checkScroll();
    }, 500);
  }
  
  @HostListener('window:scroll', ['$event'])
  checkScroll() {
    const featuresSection = document.getElementById('features');
    const howItWorksSection = document.getElementById('how-it-works');
    
    if (featuresSection) {
      const featuresSectionPos = featuresSection.getBoundingClientRect();
      this.featuresVisible = featuresSectionPos.top < window.innerHeight - 100;
    }
    
    if (howItWorksSection) {
      const howItWorksSectionPos = howItWorksSection.getBoundingClientRect();
      this.stepsVisible = howItWorksSectionPos.top < window.innerHeight - 100;
    }
  }
}