import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { NavbarComponent } from "../../components/navbar/navbar.component";

interface Driver {
  id: number;
  name: string;
  rating: number;
  experience: number;
  specialties: string[];
  hourlyRate: number;
  available: boolean;
  imageUrl: string;
}

@Component({
  selector: 'app-driver-booking',
  templateUrl: './driver-booking.component.html',
  styleUrls: ['./driver-booking.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NavbarComponent
]
})
export class DriverBookingComponent implements OnInit {
  bookingForm!: FormGroup;
  tripDetailsForm!: FormGroup;
  driverSelectionForm!: FormGroup;
  paymentForm!: FormGroup;
  
  currentStep: number = 1;
  totalSteps: number = 3;
  
  drivers: Driver[] = [
    {
      id: 1,
      name: 'John Smith',
      rating: 4.8,
      experience: 5,
      specialties: ['Long Distance', 'Night Driving'],
      hourlyRate: 25,
      available: true,
      imageUrl: 'assets/driver1.jpg'
    },
    {
      id: 2,
      name: 'Sarah Johnson',
      rating: 4.9,
      experience: 7,
      specialties: ['City Driving', 'Airport Transfers'],
      hourlyRate: 28,
      available: true,
      imageUrl: 'assets/driver2.jpg'
    },
    {
      id: 3,
      name: 'Miguel Rodriguez',
      rating: 4.7,
      experience: 4,
      specialties: ['Highway Driving', 'Elderly Assistance'],
      hourlyRate: 23,
      available: false,
      imageUrl: 'assets/driver3.jpg'
    },
    {
      id: 4,
      name: 'Emma Williams',
      rating: 5.0,
      experience: 10,
      specialties: ['Luxury Vehicles', 'VIP Service'],
      hourlyRate: 35,
      available: true,
      imageUrl: 'assets/driver4.jpg'
    }
  ];

  availableDrivers: Driver[] = [];
  selectedDriver: Driver | null = null;
  minDate: string = '';
  totalCost = 0;
  bookingSuccess = false;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.createForms();
    this.updateAvailableDrivers();
    
    // Set min date to today
    const today = new Date();
    this.minDate = today.toISOString().split('T')[0];
    
    // Update total cost when trip details change
    this.tripDetailsForm.valueChanges.subscribe(() => {
      this.calculateTotalCost();
    });
    
    // Update driver selection when pickup date/time changes
    this.tripDetailsForm.get('pickupDate')?.valueChanges.subscribe(() => {
      this.updateAvailableDrivers();
    });
  }

  createForms(): void {
    // Trip details form
    this.tripDetailsForm = this.fb.group({
      pickupLocation: ['', Validators.required],
      destination: ['', Validators.required],
      pickupDate: ['', Validators.required],
      pickupTime: ['', Validators.required],
      estimatedDuration: [1, [Validators.required, Validators.min(1)]],
      vehicleType: ['', Validators.required],
      additionalInfo: ['']
    });

    // Driver selection form
    this.driverSelectionForm = this.fb.group({
      driverId: [null, Validators.required]
    });

    // Payment form
    this.paymentForm = this.fb.group({
      cardholderName: ['', Validators.required],
      cardNumber: ['', [Validators.required, Validators.pattern('^[0-9]{16}$')]],
      expiryDate: ['', [Validators.required, Validators.pattern('^(0[1-9]|1[0-2])\/[0-9]{2}$')]],
      cvv: ['', [Validators.required, Validators.pattern('^[0-9]{3,4}$')]]
    });

    // Combined booking form
    this.bookingForm = this.fb.group({
      tripDetails: this.tripDetailsForm,
      driverSelection: this.driverSelectionForm,
      payment: this.paymentForm
    });
  }

  updateAvailableDrivers(): void {
    // In a real app, this would filter drivers based on availability for the selected date/time
    this.availableDrivers = this.drivers.filter(driver => driver.available);
  }

  selectDriver(driverId: number): void {
    this.selectedDriver = this.drivers.find(d => d.id === driverId) || null;
    this.driverSelectionForm.get('driverId')?.setValue(driverId);
    this.calculateTotalCost();
  }

  calculateTotalCost(): void {
    if (this.selectedDriver && this.tripDetailsForm.get('estimatedDuration')?.valid) {
      const hours = this.tripDetailsForm.get('estimatedDuration')?.value;
      this.totalCost = this.selectedDriver.hourlyRate * hours;
    } else {
      this.totalCost = 0;
    }
  }

  submitBooking(): void {
    if (this.bookingForm.valid) {
      // In a real app, this would send the booking data to a backend service
      console.log('Booking submitted:', this.bookingForm.value);
      this.bookingSuccess = true;
    } else {
      // Mark all fields as touched to trigger validation messages
      this.markFormGroupTouched(this.bookingForm);
    }
  }

  // Utility to mark all controls in a form group as touched
  markFormGroupTouched(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      } else {
        control?.markAsTouched();
      }
    });
  }

  nextStep(): void {
    if (this.currentStep === 1 && this.tripDetailsForm.valid) {
      this.currentStep++;
    } else if (this.currentStep === 2 && this.driverSelectionForm.valid) {
      this.currentStep++;
    }
  }

  prevStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  resetForm(): void {
    this.bookingForm.reset();
    this.selectedDriver = null;
    this.totalCost = 0;
    this.currentStep = 1;
    this.bookingSuccess = false;
  }

  // Utility for star rating display
  getStars(rating: number): number[] {
    return Array(Math.floor(rating)).fill(0);
  }

  // For easy access to form controls in the template
  get tripControls() {
    return this.tripDetailsForm.controls;
  }

  get driverControls() {
    return this.driverSelectionForm.controls;
  }

  get paymentControls() {
    return this.paymentForm.controls;
  }

  // Check if steps are valid
  isTripDetailsValid(): boolean {
    return this.tripDetailsForm.valid;
  }

  isDriverSelectionValid(): boolean {
    return this.driverSelectionForm.valid;
  }

  isPaymentValid(): boolean {
    return this.paymentForm.valid;
  }
}