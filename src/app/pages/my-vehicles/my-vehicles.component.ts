import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";

interface Vehicle {
  id: number;
  make: string;
  model: string;
  year: number;
  licensePlate: string;
  color: string;
  type: string;
}

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
  templateUrl: './my-vehicles.component.html',
  styleUrls: ['./my-vehicles.component.css']
})
export class MyVehiclesComponent implements OnInit {
  isLoading: boolean = false;
  errorMessage: string = '';
  vehicles: Vehicle[] = [];
  showAddForm: boolean = false;
  vehicleForm: FormGroup;
  
  sidebarMenuItems = [
    { label: 'Dashboard' , route: '/user-dashboard'},
    { label: 'My Bookings' },
    { label: 'Find Driver' },
    { label: 'My Vehicles', active: true },
    { label: 'Support' },
    { label: 'Settings' },
    { label: 'My Profile' },
  ];

  constructor(private fb: FormBuilder) {
    this.vehicleForm = this.fb.group({
      make: ['', Validators.required],
      model: ['', Validators.required],
      year: ['', [Validators.required, Validators.min(1980), Validators.max(new Date().getFullYear())]],
      licensePlate: ['', Validators.required],
      color: ['', Validators.required],
      type: ['', Validators.required]
    });
  }
  
  ngOnInit(): void {
    // Mock data for display
    this.vehicles = [
      {
        id: 1,
        make: 'Toyota',
        model: 'Camry',
        year: 2019,
        licensePlate: 'ABC123',
        color: 'Black',
        type: 'Sedan'
      },
      {
        id: 2,
        make: 'Honda',
        model: 'Accord',
        year: 2020,
        licensePlate: 'XYZ789',
        color: 'White',
        type: 'Sedan'
      }
    ];
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
    
    // Mock adding a vehicle
    const newVehicle: Vehicle = {
      id: this.vehicles.length + 1,
      ...this.vehicleForm.value
    };
    
    // Simulate API call
    setTimeout(() => {
      this.vehicles.push(newVehicle);
      this.isLoading = false;
      this.showAddForm = false;
      this.vehicleForm.reset();
    }, 1000);
  }
  
  removeVehicle(id: number): void {
    if (confirm('Are you sure you want to remove this vehicle?')) {
      this.isLoading = true;
      
      // Simulate API call
      setTimeout(() => {
        this.vehicles = this.vehicles.filter(v => v.id !== id);
        this.isLoading = false;
      }, 1000);
    }
  }
  
  // Method to clear error messages
  clearError(): void {
    this.errorMessage = '';
  }
}