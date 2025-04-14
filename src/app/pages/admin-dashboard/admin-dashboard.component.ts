import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, NgModel, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import { AdminNavbarComponent } from '../../components/admin-navbar/admin-navbar.component';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  imports: [AdminNavbarComponent, CommonModule, FormsModule, ReactiveFormsModule, HttpClientModule],
  standalone: true,
  providers: [AdminService,  FormBuilder],
})
export class AdminDashboardComponent implements OnInit {
  activeTab = 'users';
  searchTerm = '';
  showUserModal = false;
  showDriverModal = false;
  confirmDeleteModal = false;
  isEditMode = false;
  
  users: any[] = [];
  filteredUsers: any[] = [];
  drivers: any[] = [];
  filteredDrivers: any[] = [];
  
  userForm: FormGroup;
  driverForm: FormGroup;
  
  entityToDelete: any = null;
  
  stats: any = {
    totalUsers: 0,
    totalDrivers: 0,
    totalBookings: 0
  };

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder,
  ) {
    this.userForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required]],
      aadharCard: ['', [Validators.required]],
      accountStatus: ['ACTIVE']
    });
    
    this.driverForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required]],
      aadharCard: ['', [Validators.required]],
      licenseNumber: ['', Validators.required],
      status: ['AVAILABLE'],
      accountStatus: ['ACTIVE']
    });
  }

  ngOnInit(): void {
    this.loadDashboardData();
    this.loadUsers();
    this.loadDrivers();
  }
  
  loadDashboardData(): void {
    this.adminService.getStats().subscribe({
      next: (data: any) => {
        this.stats = data;
      },
      error: (err: Error) => {
        console.error('Error loading dashboard stats:', err);
      }
    });
  }
  
  loadUsers(): void {
    this.adminService.getAllUsers().subscribe({
      next: (data: any) => {
        this.users = data.map((user: any) => ({
          userId: user.userId,
          fullName: user.fullName,
          email: user.email,
          phone: user.phone,
          aadharCard: user.aadharCard,
          averageRating: user.averageRating || 'N/A',
          accountStatus: user.status,
          totalBookings: user.totalBookings,
          createdAt: new Date(user.createdAt).toLocaleDateString()
        }));
        this.filteredUsers = [...this.users];
      },
      error: (err: Error) => {
        console.error('Error loading users:', err);
      }
    });
  }
  
  loadDrivers(): void {
    this.adminService.getAllDrivers().subscribe({
      next: (data: any) => {
        this.drivers = data.map((driver: any) => ({
          driverId: driver.driverId,
          name: driver.name,
          email: driver.email,
          phone: driver.phone,
          aadharCard: driver.aadharCard,
          licenseNumber: driver.licenseNumber,
          status: driver.status,
          accountStatus: driver.accountStatus,
          averageRating: driver.averageRating || 'N/A',
          totalTrips: driver.totalTrips
        }));
        this.filteredDrivers = [...this.drivers];
      },
      error: (err: Error) => {
        console.error('Error loading drivers:', err);
      }
    });
  }
  
  switchTab(tab: string): void {
    this.activeTab = tab;
    this.searchTerm = '';
    if (tab === 'users') {
      this.filteredUsers = [...this.users];
    } else {
      this.filteredDrivers = [...this.drivers];
    }
  }
  
  searchUsers(): void {
    const term = this.searchTerm.toLowerCase().trim();
    
    if (this.activeTab === 'users') {
      if (!term) {
        this.filteredUsers = [...this.users];
      } else {
        this.filteredUsers = this.users.filter(user => 
          user.fullName.toLowerCase().includes(term) ||
          user.email.toLowerCase().includes(term) ||
          user.phone.includes(term)
        );
      }
    } else {
      if (!term) {
        this.filteredDrivers = [...this.drivers];
      } else {
        this.filteredDrivers = this.drivers.filter(driver => 
          driver.name.toLowerCase().includes(term) ||
          driver.email.toLowerCase().includes(term) ||
          driver.phone.includes(term)
        );
      }
    }
  }
  
  getStatusClass(status: string): string {
    const statusLower = status.toLowerCase();
    if (statusLower === 'active') return 'active';
    if (statusLower === 'inactive') return 'inactive';
    if (statusLower === 'suspended') return 'suspended';
    return '';
  }
  
  addNewUser(): void {
    this.isEditMode = false;
    this.userForm.reset({
      accountStatus: 'ACTIVE'
    });
    this.showUserModal = true;
  }
  
  editUser(user: any): void {
    this.isEditMode = true;
    this.userForm.patchValue({
      fullName: user.fullName,
      email: user.email,
      phone: user.phone,
      aadharCard: user.aadharCard,
      accountStatus: user.accountStatus
    });
    this.userForm.get('userId')?.setValue(user.userId);
    this.showUserModal = true;
  }
  
  saveUser(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }
    
    const userData = this.userForm.value;
    
    if (this.isEditMode) {
      const userId = this.userForm.get('userId')?.value;
      this.adminService.updateUser(userId, userData).subscribe({
        next: () => {
          this.closeUserModal();
          this.loadUsers();
        },
        error: (err) => {
          console.error('Error updating user:', err);
        }
      });
    } else {
      // For new users, would need a registration endpoint
      this.closeUserModal();
    }
  }
  
  closeUserModal(): void {
    this.showUserModal = false;
    this.userForm.reset();
  }
  
  addNewDriver(): void {
    this.isEditMode = false;
    this.driverForm.reset({
      status: 'AVAILABLE',
      accountStatus: 'ACTIVE'
    });
    this.showDriverModal = true;
  }
  
  editDriver(driver: any): void {
    this.isEditMode = true;
    this.driverForm.patchValue({
      name: driver.name,
      email: driver.email,
      phone: driver.phone,
      aadharCard: driver.aadharCard,
      licenseNumber: driver.licenseNumber,
      status: driver.status,
      accountStatus: driver.accountStatus
    });
    this.driverForm.get('driverId')?.setValue(driver.driverId);
    this.showDriverModal = true;
  }
  
  saveDriver(): void {
    if (this.driverForm.invalid) {
      this.driverForm.markAllAsTouched();
      return;
    }
    
    const driverData = this.driverForm.value;
    
    if (this.isEditMode) {
      const driverId = this.driverForm.get('driverId')?.value;
      this.adminService.updateDriver(driverId, driverData).subscribe({
        next: () => {
          this.closeDriverModal();
          this.loadDrivers();
        },
        error: (err: Error) => {
          console.error('Error updating driver:', err);
        }
      });
    } else {
      // For new drivers, would need a registration endpoint
      this.closeDriverModal();
    }
  }
  
  closeDriverModal(): void {
    this.showDriverModal = false;
    this.driverForm.reset();
  }
  
  confirmDelete(type: string, id: number): void {
    this.entityToDelete = { type, id };
    this.confirmDeleteModal = true;
  }
  
  deleteEntity(): void {
    if (!this.entityToDelete) return;
    
    if (this.entityToDelete.type === 'user') {
      this.adminService.deleteUser(this.entityToDelete.id).subscribe({
        next: () => {
          this.loadUsers();
          this.closeDeleteModal();
        },
        error: (err: Error) => {
          console.error('Error deleting user:', err);
        }
      });
    } else if (this.entityToDelete.type === 'driver') {
      this.adminService.deleteDriver(this.entityToDelete.id).subscribe({
        next: () => {
          this.loadDrivers();
          this.closeDeleteModal();
        },
        error: (err: Error) => {
          console.error('Error deleting driver:', err);
        }
      });
    }
  }
  
  closeDeleteModal(): void {
    this.confirmDeleteModal = false;
    this.entityToDelete = null;
  }
}