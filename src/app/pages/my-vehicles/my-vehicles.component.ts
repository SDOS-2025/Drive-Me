import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { VehicleService, BackendVehicle } from '../../services/vehicle.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-my-vehicles',
  standalone: true,
  imports: [
    CommonModule, 
    SidebarComponent, 
    DashboardNavbarComponent, 
    RouterModule, 
    HttpClientModule,
    ReactiveFormsModule
  ],
  providers: [VehicleService],
  templateUrl: './my-vehicles.component.html',
  styleUrls: ['./my-vehicles.component.css']
})
export class MyVehiclesComponent implements OnInit {
  isLoading: boolean = false;
  errorMessage: string = '';
  vehicles: BackendVehicle[] = [];
  showAddForm: boolean = false;
  vehicleForm: FormGroup;
  userId: number = 0;
  
  sidebarMenuItems = [
    { label: 'Dashboard' , route: '/user-dashboard'},
    { label: 'My Bookings' },
    { label: 'Find Driver' },
    { label: 'My Vehicles', active: true },
    { label: 'Support' },
    { label: 'Settings' },
    { label: 'My Profile' },
  ];

  constructor(
    private fb: FormBuilder,
    private vehicleService: VehicleService,
    private authService: AuthService
  ) {
    this.vehicleForm = this.fb.group({
      model: ['', Validators.required],
      registerationNumber: ['', Validators.required],
      carNumber: ['', Validators.required],
    });
  }
  
  ngOnInit(): void {
    this.getCurrentUser();
    this.loadUserVehicles();
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
      next: (backendVehicles) => {
        // Map backend vehicles to frontend format
        this.vehicles = backendVehicles
          .filter(v => v.userId === this.userId) // Only show current user's vehicles
          .map(v => {
            
            const model = v.model || 'Not specified'; // Default if model not available
            const registerationNumber = v.registerationNumber || 'Not specified'; // Default if registerationNumber not available
            const carNumber = v.carNumber || 'Not specified'; // Default if carNumber not available
            return {
              id: v.id,
              model: model,
              registerationNumber: registerationNumber,
              carNumber: carNumber,
              userId: v.userId,
            };
          });
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading vehicles:', error);
        this.errorMessage = 'Unable to load vehicles. Please try again later.';
        this.isLoading = false;
      }
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (this.showAddForm === false) {
      this.vehicleForm.reset();
    }
  }
  
  onSubmit(): void {
    if (this.vehicleForm.invalid) {
      return;
    }
    
    this.isLoading = true;
    
      this.vehicleService.addVehicle(this.userId, this.vehicleForm.value).subscribe({
  next: (response) => {
    console.log('Vehicle added successfully:', response); // Inspect the response
    if (response) {
      this.loadUserVehicles(); // Reload vehicles only if response is valid
      this.showAddForm = false;
      this.vehicleForm.reset();
    } else {
      console.warn('Empty response received');
      this.errorMessage = 'Vehicle added, but no data returned.';
      this.isLoading = false;
    }
  },
  error: (error) => {
    console.error('Error adding vehicle:', error);
    this.errorMessage = 'Failed to add vehicle: ' + (error.message || 'Unknown error');
    this.isLoading = false;
  }
});
  }
  
  removeVehicle(id: number): void {
    if (confirm('Are you sure you want to remove this vehicle?')) {
      this.isLoading = true;
      
      this.vehicleService.removeVehicle(id).subscribe({
        next: () => {
          console.log('Vehicle removed successfully');
          this.vehicles = this.vehicles.filter(v => v.id !== id);
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error removing vehicle:', error);
          this.errorMessage = 'Failed to remove vehicle. Please try again.';
          this.isLoading = false;
        }
      });
    }
  }
  
  // Method to clear error messages
  clearError(): void {
    this.errorMessage = '';
  }
}