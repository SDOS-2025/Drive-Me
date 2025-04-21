import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { DriverService } from '../../services/driver.service';
import { BookingRequest, BookingService } from '../../services/bookings.service';
import { VehicleService, BackendVehicle } from '../../services/vehicle.service';
import { Router } from '@angular/router';
import * as QRCode from 'qrcode';

interface Driver {
  id: number;
  name: string;
  rating: number;
  experience: number;
  specialties?: string[];
  hourlyRate: number;
  available: boolean;
  imageUrl?: string;
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
    DashboardNavbarComponent,
    HttpClientModule
  ],
  providers: [BookingService, AuthService, DriverService, VehicleService],
})
export class DriverBookingComponent implements OnInit {

  // Add these properties to your component class
  upiQrCodeSrc: string = '';
  upiDeepLink: string = '';
  bookingReference: string = '';

  // Payment confirmation
  fileError: string = '';
  selectedFile: File = new File([], '');

  //Form Details
  bookingForm!: FormGroup;
  tripDetailsForm!: FormGroup;
  driverSelectionForm!: FormGroup;
  paymentForm!: FormGroup;

  userId: number = 0;
  isLoading: boolean = false;
  errorMessage: string = '';
  currentStep: number = 1;
  totalSteps: number = 3;

  // Driver and vehicle data
  drivers: Driver[] = [];
  availableDrivers: Driver[] = [];
  selectedDriver: Driver | null = null;

  // User's vehicles
  userVehicles: BackendVehicle[] = [];
  selectedVehicle: BackendVehicle | null = null;

  minDate: string = '';
  totalCost = 0;
  bookingSuccess = false;

  constructor(
    private fb: FormBuilder,
    private bookingService: BookingService,
    private authService: AuthService,
    private driverService: DriverService,
    private vehicleService: VehicleService,
    private router: Router
  ) {
    console.log('Component constructor called');
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      // Validate file type (must be an image)
      if (!file.type.startsWith('image/')) {
        this.fileError = 'Please upload a valid image file.';
        return;
      }

      // Validate file size (e.g., max 5MB)
      const maxSizeInMB = 5;
      if (file.size > maxSizeInMB * 1024 * 1024) {
        this.fileError = `File size must not exceed ${maxSizeInMB} MB.`;
        return;
      }

      this.fileError = '';
      this.selectedFile = file;
    }
  }
  generateUpiQrCode(): void {
    if (!this.totalCost) {
      return;
    }

    // Create a unique reference ID for this booking
    this.bookingReference = `DRIVEME${this.userId}${new Date().getTime().toString().slice(-6)}`;

    // Calculate the amount in INR
    const amountInr = Math.round(this.totalCost);

    // Create UPI deep link
    this.upiDeepLink = `upi://pay?pa=harshmistry159@okhdfcbank&pn=DriveMe&am=${amountInr}&cu=INR&tn=${this.bookingReference}`;

    // Generate QR code
    QRCode.toDataURL(this.upiDeepLink, {
      errorCorrectionLevel: 'H',
      margin: 1,
      width: 250,
      color: {
        dark: '#18004c',  // Use your app's primary color
        light: '#ffffff'
      }
    })
      .then(url => {
        this.upiQrCodeSrc = url;
      })
      .catch(err => {
        console.error('Error generating QR code:', err);
        this.errorMessage = 'Failed to generate payment QR code';
      });
  }


  ngOnInit(): void {
    console.log('ngOnInit called');
    try {
      // Get current user and initialize
      this.getCurrentUser();
      this.createForms();
      this.loadUserVehicles();
      this.loadAvailableDrivers();

      // Set min date to today
      const today = new Date();
      this.minDate = today.toISOString().split('T')[0];

      // Form subscriptions
      if (this.tripDetailsForm) {
        this.tripDetailsForm.valueChanges.subscribe(() => {
          this.calculateTotalCost();
        });

        this.tripDetailsForm.get('pickupDate')?.valueChanges.subscribe(() => {
          this.updateAvailableDrivers();
        });
      }
    } catch (error) {
      console.error('Error in ngOnInit:', error);
      this.errorMessage = 'Error initializing booking page: ' + (error as Error).message;
    }
  }

  getCurrentUser(): void {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userId = currentUser.id || 0;
      console.log('Current User ID:', this.userId);
    }
  }

  loadUserVehicles(): void {
    this.isLoading = true;
    this.vehicleService.getUserVehicles().subscribe({
      next: (vehicles) => {
        console.log('All vehicles loaded:', vehicles);
        // Filter vehicles belonging to current user
        this.userVehicles = vehicles.filter(v => v.userId === this.userId);
        console.log('User vehicles filtered:', this.userVehicles);
        console.log('User ID for filtering:', this.userId);
        this.isLoading = false;

        // If there's only one vehicle, automatically select it
        if (this.userVehicles.length === 1) {
          this.selectVehicle(this.userVehicles[0].id);
          this.tripDetailsForm.get('vehicleId')?.setValue(this.userVehicles[0].id);
          console.log('Auto-selected vehicle:', this.userVehicles[0]);
        }
      },
      error: (error) => {
        console.error('Error loading vehicles:', error);
        this.errorMessage = 'Failed to load your vehicles. Please try again.';
        this.isLoading = false;
      }
    });
  }


  loadAvailableDrivers(): void {
    this.isLoading = true;
    this.driverService.getDriverStatus().subscribe({
      next: (data) => {
        // Map API data to our Driver interface
        this.drivers = data.map(driver => ({
          id: driver.driverId,
          name: driver.name,
          rating: 4.5,
          experience: 3, // Default if not available from API
          hourlyRate: 150, // Default if not available from API
          available: driver.status === 'AVAILABLE'
        }));

        // Filter available drivers
        this.updateAvailableDrivers();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching drivers', error);
        this.errorMessage = 'Failed to load drivers. Please try again.';
        this.isLoading = false;

        // Fallback to sample data if API fails
        this.updateAvailableDrivers();
      }
    });
  }

  createForms(): void {
    // Trip details form with vehicle selection
    this.tripDetailsForm = this.fb.group({
      pickupLocation: ['', Validators.required],
      destination: ['', Validators.required],
      pickupDate: ['', Validators.required],
      pickupTime: ['', Validators.required],
      estimatedDuration: [1, [Validators.required, Validators.min(1)]],
      vehicleId: ['', Validators.required], // Changed from vehicleType to vehicleId
      additionalInfo: ['']
    });

    // Driver selection form
    this.driverSelectionForm = this.fb.group({
      driverId: [null, Validators.required]
    });

    // Payment form - simplified for UPI payment
    this.paymentForm = this.fb.group({
      paymentConfirmed: [false, Validators.requiredTrue]
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

  selectVehicle(vehicleId: any): void {
    console.log('selectVehicle called with ID:', vehicleId);

    if (!vehicleId) {
      console.log('No vehicle ID provided, clearing selection');
      this.selectedVehicle = null;
      return;
    }

    // Convert string to number if needed
    const id = typeof vehicleId === 'string' ? parseInt(vehicleId, 10) : vehicleId;
    console.log('Looking for vehicle with ID:', id);
    console.log('Available vehicles:', this.userVehicles);

    this.selectedVehicle = this.userVehicles.find(v => v.id === id) || null;
    console.log('Found and selected vehicle:', this.selectedVehicle);
  }

  calculateTotalCost(): void {
    if (this.selectedDriver && this.tripDetailsForm.get('estimatedDuration')?.valid) {
      const hours = this.tripDetailsForm.get('estimatedDuration')?.value;
      this.totalCost = this.selectedDriver.hourlyRate * hours;
      this.generateUpiQrCode(); // Generate QR code whenever cost is calculated
    } else {
      this.totalCost = 0;
      this.upiQrCodeSrc = ''; // Clear QR code if no valid driver or duration
    }
  }

  submitBooking(): void {
    if (this.bookingForm.valid && this.selectedDriver && this.selectedVehicle) {
      this.isLoading = true;

      // Create booking request
      const bookingRequest: BookingRequest = {
        customer: { id: this.userId },
        driver: { driver_id: this.selectedDriver.id },
        vehicle: { id: this.selectedVehicle.id },
        pickupLocation: this.tripDetailsForm.get('pickupLocation')?.value,
        dropoffLocation: this.tripDetailsForm.get('destination')?.value,
        // Format date and time for backend
        pickupDateTime: `${this.tripDetailsForm.get('pickupDate')?.value} ${this.tripDetailsForm.get('pickupTime')?.value}`,
        fare: this.totalCost,
        estimatedDuration: this.tripDetailsForm.get('estimatedDuration')?.value,
      };

      this.bookingService.createBooking(bookingRequest, this.selectedFile).subscribe({
        next: (bookingResponse) => {
          console.log('Booking created:', bookingResponse);
          this.bookingSuccess = true;
          this.isLoading = false;
          this.router.navigate(['/user-dashboard']);
        },
        error: (error) => {
          console.error('Booking error:', error);
          this.errorMessage = 'Booking failed. Please try again.';
          this.isLoading = false;
        }
      });
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
      this.generateUpiQrCode();
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
    this.selectedVehicle = null;
    this.totalCost = 0;
    this.currentStep = 1;
    this.bookingSuccess = false;
    this.errorMessage = '';
    this.isLoading = false;
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
    return this.paymentForm.get('paymentConfirmed')?.value === true;
  }

  isFileUploaded(): boolean {
    return this.selectedFile && this.selectedFile.size > 0;
  }
}