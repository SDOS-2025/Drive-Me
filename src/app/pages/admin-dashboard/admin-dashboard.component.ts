import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminNavbarComponent } from "../../components/admin-navbar/admin-navbar.component";

interface User {
  id: number;
  name: string;
  email: string;
  phone: string;
  joinDate: string;
  bookingsCount: number;
  status: 'active' | 'inactive' | 'suspended';
}

interface Driver {
  id: number;
  name: string;
  email: string;
  phone: string;
  rating: number;
  experience: number;
  specialties: string[];
  hourlyRate: number;
  status: 'active' | 'inactive' | 'suspended';
  totalTrips: number;
  joinDate: string;
  licenseNumber: string;
  vehicleInfo: string;
}

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    AdminNavbarComponent
]
})
export class AdminDashboardComponent implements OnInit {
  activeTab: 'users' | 'drivers' = 'users';
  userForm!: FormGroup;
  driverForm!: FormGroup;
  searchTerm: string = '';
  isEditMode: boolean = false;
  currentUserId: number | null = null;
  currentDriverId: number | null = null;
  showUserModal = false;
  showDriverModal = false;
  confirmDeleteModal = false;
  entityToDelete: { type: 'user' | 'driver', id: number } | null = null;
  
  users: User[] = [
    {
      id: 1,
      name: 'John Doe',
      email: 'john.doe@example.com',
      phone: '555-123-4567',
      joinDate: '2023-01-15',
      bookingsCount: 12,
      status: 'active'
    },
    {
      id: 2,
      name: 'Jane Smith',
      email: 'jane.smith@example.com',
      phone: '555-987-6543',
      joinDate: '2023-03-22',
      bookingsCount: 5,
      status: 'active'
    },
    {
      id: 3,
      name: 'Robert Johnson',
      email: 'robert.j@example.com',
      phone: '555-456-7890',
      joinDate: '2023-05-10',
      bookingsCount: 0,
      status: 'inactive'
    },
    {
      id: 4,
      name: 'Emily Davis',
      email: 'emily.davis@example.com',
      phone: '555-222-3333',
      joinDate: '2023-02-28',
      bookingsCount: 8,
      status: 'active'
    },
    {
      id: 5,
      name: 'Michael Wilson',
      email: 'michael.w@example.com',
      phone: '555-888-9999',
      joinDate: '2023-04-12',
      bookingsCount: 3,
      status: 'suspended'
    }
  ];

  drivers: Driver[] = [
    {
      id: 1,
      name: 'John Smith',
      email: 'john.smith@example.com',
      phone: '555-111-2222',
      rating: 4.8,
      experience: 5,
      specialties: ['Long Distance', 'Night Driving'],
      hourlyRate: 25,
      status: 'active',
      totalTrips: 156,
      joinDate: '2022-06-15',
      licenseNumber: 'DL12345678',
      vehicleInfo: 'Toyota Camry 2020'
    },
    {
      id: 2,
      name: 'Sarah Johnson',
      email: 'sarah.j@example.com',
      phone: '555-333-4444',
      rating: 4.9,
      experience: 7,
      specialties: ['City Driving', 'Airport Transfers'],
      hourlyRate: 28,
      status: 'active',
      totalTrips: 213,
      joinDate: '2021-11-05',
      licenseNumber: 'DL87654321',
      vehicleInfo: 'Honda Accord 2021'
    },
    {
      id: 3,
      name: 'Miguel Rodriguez',
      email: 'miguel.r@example.com',
      phone: '555-555-6666',
      rating: 4.7,
      experience: 4,
      specialties: ['Highway Driving', 'Elderly Assistance'],
      hourlyRate: 23,
      status: 'inactive',
      totalTrips: 87,
      joinDate: '2023-01-20',
      licenseNumber: 'DL22334455',
      vehicleInfo: 'Hyundai Sonata 2019'
    },
    {
      id: 4,
      name: 'Emma Williams',
      email: 'emma.w@example.com',
      phone: '555-777-8888',
      rating: 5.0,
      experience: 10,
      specialties: ['Luxury Vehicles', 'VIP Service'],
      hourlyRate: 35,
      status: 'active',
      totalTrips: 342,
      joinDate: '2020-03-15',
      licenseNumber: 'DL99887766',
      vehicleInfo: 'BMW 5 Series 2022'
    }
  ];

  filteredUsers: User[] = [];
  filteredDrivers: Driver[] = [];

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.initForms();
    this.filteredUsers = [...this.users];
    this.filteredDrivers = [...this.drivers];
  }

  initForms(): void {
    this.userForm = this.fb.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^\d{3}-\d{3}-\d{4}$/)]],
      status: ['active', [Validators.required]]
    });

    this.driverForm = this.fb.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^\d{3}-\d{3}-\d{4}$/)]],
      experience: [0, [Validators.required, Validators.min(0)]],
      hourlyRate: [0, [Validators.required, Validators.min(0)]],
      status: ['active', [Validators.required]],
      licenseNumber: ['', [Validators.required]],
      vehicleInfo: ['', [Validators.required]],
      specialties: ['', [Validators.required]]
    });
  }

  switchTab(tab: 'users' | 'drivers'): void {
    this.activeTab = tab;
    this.searchTerm = '';
    this.filterEntities();
  }

  searchUsers(): void {
    this.filterEntities();
  }

  filterEntities(): void {
    const term = this.searchTerm.toLowerCase();
    
    if (this.activeTab === 'users') {
      this.filteredUsers = this.users.filter(user => 
        user.name.toLowerCase().includes(term) || 
        user.email.toLowerCase().includes(term) ||
        user.phone.includes(term)
      );
    } else {
      this.filteredDrivers = this.drivers.filter(driver => 
        driver.name.toLowerCase().includes(term) || 
        driver.email.toLowerCase().includes(term) ||
        driver.phone.includes(term)
      );
    }
  }

  addNewUser(): void {
    this.isEditMode = false;
    this.currentUserId = null;
    this.userForm.reset({status: 'active'});
    this.showUserModal = true;
  }

  editUser(user: User): void {
    this.isEditMode = true;
    this.currentUserId = user.id;
    this.userForm.patchValue({
      name: user.name,
      email: user.email,
      phone: user.phone,
      status: user.status
    });
    this.showUserModal = true;
  }

  addNewDriver(): void {
    this.isEditMode = false;
    this.currentDriverId = null;
    this.driverForm.reset({
      status: 'active',
      experience: 0,
      hourlyRate: 0
    });
    this.showDriverModal = true;
  }

  editDriver(driver: Driver): void {
    this.isEditMode = true;
    this.currentDriverId = driver.id;
    this.driverForm.patchValue({
      name: driver.name,
      email: driver.email,
      phone: driver.phone,
      experience: driver.experience,
      hourlyRate: driver.hourlyRate,
      status: driver.status,
      licenseNumber: driver.licenseNumber,
      vehicleInfo: driver.vehicleInfo,
      specialties: driver.specialties.join(', ')
    });
    this.showDriverModal = true;
  }

  confirmDelete(type: 'user' | 'driver', id: number): void {
    this.entityToDelete = { type, id };
    this.confirmDeleteModal = true;
  }

  deleteEntity(): void {
    if (!this.entityToDelete) return;
    
    if (this.entityToDelete.type === 'user') {
      this.users = this.users.filter(user => user.id !== this.entityToDelete?.id);
      this.filteredUsers = this.filteredUsers.filter(user => user.id !== this.entityToDelete?.id);
    } else {
      this.drivers = this.drivers.filter(driver => driver.id !== this.entityToDelete?.id);
      this.filteredDrivers = this.filteredDrivers.filter(driver => driver.id !== this.entityToDelete?.id);
    }
    
    this.closeDeleteModal();
  }

  saveUser(): void {
    if (this.userForm.invalid) {
      this.markFormGroupTouched(this.userForm);
      return;
    }

    const formValue = this.userForm.value;
    
    if (this.isEditMode && this.currentUserId) {
      // Edit existing user
      const userIndex = this.users.findIndex(u => u.id === this.currentUserId);
      if (userIndex !== -1) {
        this.users[userIndex] = {
          ...this.users[userIndex],
          name: formValue.name,
          email: formValue.email,
          phone: formValue.phone,
          status: formValue.status
        };
      }
    } else {
      // Add new user
      const newUser: User = {
        id: this.users.length ? Math.max(...this.users.map(u => u.id)) + 1 : 1,
        name: formValue.name,
        email: formValue.email,
        phone: formValue.phone,
        status: formValue.status,
        joinDate: new Date().toISOString().split('T')[0],
        bookingsCount: 0
      };
      this.users.push(newUser);
    }
    
    this.filteredUsers = [...this.users];
    this.closeUserModal();
  }

  saveDriver(): void {
    if (this.driverForm.invalid) {
      this.markFormGroupTouched(this.driverForm);
      return;
    }

    const formValue = this.driverForm.value;
    const specialtiesArray = formValue.specialties.split(',').map((s: string) => s.trim());
    
    if (this.isEditMode && this.currentDriverId) {
      // Edit existing driver
      const driverIndex = this.drivers.findIndex(d => d.id === this.currentDriverId);
      if (driverIndex !== -1) {
        this.drivers[driverIndex] = {
          ...this.drivers[driverIndex],
          name: formValue.name,
          email: formValue.email,
          phone: formValue.phone,
          experience: formValue.experience,
          hourlyRate: formValue.hourlyRate,
          status: formValue.status,
          licenseNumber: formValue.licenseNumber,
          vehicleInfo: formValue.vehicleInfo,
          specialties: specialtiesArray
        };
      }
    } else {
      // Add new driver
      const newDriver: Driver = {
        id: this.drivers.length ? Math.max(...this.drivers.map(d => d.id)) + 1 : 1,
        name: formValue.name,
        email: formValue.email,
        phone: formValue.phone,
        experience: formValue.experience,
        hourlyRate: formValue.hourlyRate,
        status: formValue.status,
        licenseNumber: formValue.licenseNumber,
        vehicleInfo: formValue.vehicleInfo,
        specialties: specialtiesArray,
        rating: 0,
        totalTrips: 0,
        joinDate: new Date().toISOString().split('T')[0]
      };
      this.drivers.push(newDriver);
    }
    
    this.filteredDrivers = [...this.drivers];
    this.closeDriverModal();
  }

  closeUserModal(): void {
    this.showUserModal = false;
    this.userForm.reset({status: 'active'});
  }

  closeDriverModal(): void {
    this.showDriverModal = false;
    this.driverForm.reset({
      status: 'active',
      experience: 0,
      hourlyRate: 0
    });
  }

  closeDeleteModal(): void {
    this.confirmDeleteModal = false;
    this.entityToDelete = null;
  }

  markFormGroupTouched(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  getStatusClass(status: string): string {
    switch(status) {
      case 'active': return 'status-active';
      case 'inactive': return 'status-inactive';
      case 'suspended': return 'status-suspended';
      default: return '';
    }
  }
}