import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormsModule, Validators, Form } from '@angular/forms';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { UserBookingService } from '../../services/user.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule,  SidebarComponent, DashboardNavbarComponent],
  providers: [AuthService],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent implements OnInit {
  userType: string = 'user'; // 'user' or 'driver'
  userId: number = 0;
  userName: string = '';
  showEditProfileModal: boolean = false;
  userEmail: string = '';
  userPhone: string = '';
  isSubmitting: boolean = false;
  profileForm: Form | any;
  profileImageFile: File | null = null;
  profileImagePreview: string | null = null;
  profilePictureError: string = '';
  
  // Settings categories
  activeTab: string = 'account';
  
  userAvatar: string = '';
  
  // User settings
  notificationSettings = {
    emailNotifications: true,
    pushNotifications: true,
    smsNotifications: false,
    tripUpdates: true,
    promotionalOffers: false,
    securityAlerts: true
  };
  
  privacySettings = {
    shareLocationData: true,
    shareProfileWithDrivers: true,
    allowReviewsAndRatings: true,
    shareRideHistory: false
  };
  
  // Driver-specific settings
  driverSettings = {
    automaticallyAcceptTrips: false,
    maxTripDistance: 25,
    preferredTripTypes: ['standard', 'premium'],
    availableHours: {
      start: '08:00',
      end: '18:00'
    },
    breakTime: {
      enabled: true,
      start: '12:00',
      duration: 60 // minutes
    }
  };
  
  // Account settings
  accountSettings = {
    language: 'english',
    currency: 'usd',
    darkMode: false
  };
  
  // Languages and currencies for selection
  availableLanguages = [
    { value: 'english', label: 'English' },
    { value: 'spanish', label: 'Spanish' },
    { value: 'french', label: 'French' },
    { value: 'german', label: 'German' },
    { value: 'chinese', label: 'Chinese' },
    { value: 'japanese', label: 'Japanese' },
    { value: 'arabic', label: 'Arabic' },
    { value: 'hindi', label: 'Hindi' }
  ];
  
  availableCurrencies = [
    { value: 'usd', label: 'US Dollar (USD)' },
    { value: 'eur', label: 'Euro (EUR)' },
    { value: 'gbp', label: 'British Pound (GBP)' },
    { value: 'jpy', label: 'Japanese Yen (JPY)' },
    { value: 'cad', label: 'Canadian Dollar (CAD)' },
    { value: 'aud', label: 'Australian Dollar (AUD)' },
    { value: 'inr', label: 'Indian Rupee (INR)' },
    { value: 'cny', label: 'Chinese Yuan (CNY)' }
  ];
  
  // Trip types for drivers
  availableTripTypes = [
    { value: 'standard', label: 'Standard' },
    { value: 'premium', label: 'Premium' },
    { value: 'express', label: 'Express' },
    { value: 'group', label: 'Group/Shared' },
    { value: 'special', label: 'Special Assistance' }
  ];
  
  // Sidebar menu items - will be set based on user type
  sidebarMenuItems: any[] = [];

  constructor(
    private authService: AuthService,
    private router: Router,
    private userBookingService: UserBookingService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.setupSidebarMenu();
    this.getProfilePicture();
    this.initProfileForm();
  }

  loadUserData(): void {
    const userData = localStorage.getItem('currentUser');
    if (userData) {
      const user = JSON.parse(userData);
      this.userName = user.fullName || 'User';
      this.userId = user.id || 0;
      
      // Determine user type
      if (user.role) {
        this.userType = user.role.toLowerCase();
      }
    }
  }

  // Get user profile picture
  getProfilePicture() {
    const filename = `${this.userId}_${this.userName}.jpg`; // Assuming the filename is userId.jpg
    
    this.userBookingService.getProfilePicture(filename).subscribe({
      next: (response: any) => {
        // Check if the response is valid (has size/type)
        if (response && response.size > 0) {
          this.userAvatar = URL.createObjectURL(response);
        } else {
          console.error('Invalid profile picture response');
          this.loadDefaultProfilePicture();
        }
      },
      error: (error) => {
        console.error('Error loading profile picture:', error);
        this.loadDefaultProfilePicture();
      }
    });
  }
  
  private loadDefaultProfilePicture() {
    console.log('Loading default profile picture...');
    this.userBookingService.getProfilePicture('default-image.jpg').subscribe({
      next: (defaultResponse: any) => {
        if (defaultResponse && defaultResponse.size > 0) {
          this.userAvatar = URL.createObjectURL(defaultResponse);
        } else {
          // As a last resort, use a hardcoded fallback image
          this.userAvatar = 'assets/images/default-avatar.png';
        }
      },
      error: (error) => {
        console.error('Error loading default profile picture:', error);
        // Use a hardcoded fallback image
        this.userAvatar = 'assets/images/default-image.png';
      }
    });
  } 

  setupSidebarMenu(): void {
    if (this.userType === 'driver') {
      this.sidebarMenuItems = [
        { label: 'Dashboard', route: '/driver-dashboard' },
        { label: 'Available Trips', route: '/available-trips' },
        { label: 'All Trips', route: '/all-trips' },
        { label: 'Support', route: '/chat-support' },
        { label: 'Settings', active: true, route: '/settings' },
      ];
    } else {
      this.sidebarMenuItems = [
        { label: 'Dashboard', route: '/user-dashboard' },
        { label: 'My Bookings', route: '/my-bookings' },
        { label: 'Find Driver', route: '/find-driver' },
        { label: 'My Vehicles', route: '/my-vehicles' },
        { label: 'Support', route: '/chat-support' },
        { label: 'Settings', active: true, route: '/settings' },
      ];
    }
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  saveSettings(): void {
    // In a real app, you would send these settings to a backend service
    console.log('Saving settings...');
    
    const settings = {
      notification: this.notificationSettings,
      privacy: this.privacySettings,
      account: this.accountSettings
    };
    
    if (this.userType === 'driver') {
      Object.assign(settings, { driver: this.driverSettings });
    }
    
    // Simulate saving
    setTimeout(() => {
      alert('Settings saved successfully!');
    }, 1000);
  }

  toggleTripType(type: string): void {
    const index = this.driverSettings.preferredTripTypes.indexOf(type);
    if (index === -1) {
      this.driverSettings.preferredTripTypes.push(type);
    } else {
      this.driverSettings.preferredTripTypes.splice(index, 1);
    }
  }

  isTripTypeSelected(type: string): boolean {
    return this.driverSettings.preferredTripTypes.includes(type);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  deleteAccount(): void {
    const confirmed = confirm('Are you sure you want to delete your account? This action cannot be undone.');
    if (confirmed) {
      // In a real app, call the service to delete the account
      console.log('Deleting account...');
      setTimeout(() => {
        alert('Your account has been deleted successfully.');
        this.authService.logout();
        this.router.navigate(['/']);
      }, 1500);
    }
  }


  initProfileForm(): void {
    this.profileForm = this.fb.group({
      fullName: [this.userName, [Validators.required, Validators.minLength(3)]],
      email: [this.userEmail, [Validators.required, Validators.email]],
      phone: [this.userPhone, [Validators.required, Validators.pattern(/^[0-9]{10,12}$/)]]
    });
  }

  // Open and close the edit profile modal
  openEditProfileModal(): void {
    console.log('Opening edit profile modal...'); 
    // Reset the form with current user data
    this.profileForm.reset({
      fullName: this.userName,
      email: this.userEmail,
      phone: this.userPhone
    });
    this.profileImageFile = null;
    this.profileImagePreview = null;
    this.profilePictureError = '';
    this.showEditProfileModal = true;
  }

  closeEditProfileModal(): void {
    this.showEditProfileModal = false;
    // Release object URL if we created one
    if (this.profileImagePreview && this.profileImagePreview !== this.userAvatar) {
      URL.revokeObjectURL(this.profileImagePreview);
      this.profileImagePreview = null;
    }
  }

  // Handle profile picture selection
  onProfilePictureSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      
      // Validate file type
      if (!file.type.match(/image\/(jpeg|jpg|png|gif)/)) {
        this.profilePictureError = 'Please select a valid image file (JPEG, PNG, or GIF).';
        return;
      }
      
      // Validate file size (max 5MB)
      const maxSizeInBytes = 5 * 1024 * 1024;
      if (file.size > maxSizeInBytes) {
        this.profilePictureError = 'Image size should be less than 5MB.';
        return;
      }
      
      // Clear any previous error
      this.profilePictureError = '';
      
      // Store the file for later upload
      this.profileImageFile = file;
      
      // Create a preview
      if (this.profileImagePreview && this.profileImagePreview !== this.userAvatar) {
        URL.revokeObjectURL(this.profileImagePreview);
      }
      this.profileImagePreview = URL.createObjectURL(file);
    }
  }

  removeProfilePicture(): void {
    this.profileImageFile = null;
    
    if (this.profileImagePreview && this.profileImagePreview !== this.userAvatar) {
      URL.revokeObjectURL(this.profileImagePreview);
    }
    
    this.profileImagePreview = null;
    this.profilePictureError = '';
  }

  // Save profile changes
  saveProfile(): void {
    this.isSubmitting = true;
    
    const formData = new FormData();
    
    // Add form fields
    formData.append('fullName', this.profileForm.get('fullName')?.value);
    formData.append('email', this.profileForm.get('email')?.value);
    formData.append('phone', this.profileForm.get('phone')?.value);
    

    if (!this.profileImageFile) {
      alert('Please select a profile picture.');
      this.isSubmitting = false;
      return;
    }
    // Send the update request
    this.userBookingService.updateUserProfile(formData, this.profileImageFile).subscribe({
      next: (response: any) => {
        console.log('Profile updated successfully:', response);
        
        // Update local user data
        const currentUser = this.authService.currentUserValue;
        if (currentUser) {
          currentUser.fullName = this.profileForm.get('fullName')?.value;
          currentUser.email = this.profileForm.get('email')?.value;
          currentUser.phone = this.profileForm.get('phone')?.value;
          
          // Update localStorage
          localStorage.setItem('currentUser', JSON.stringify(currentUser));
          
          // Update component properties
          this.userName = currentUser.fullName || 'User';
          this.userEmail = currentUser.email || '';
          this.userPhone = currentUser.phone || '';
          
          // Refresh profile picture if changed
          if (this.profileImageFile) {
            this.getProfilePicture();
          }
        }
        
        this.isSubmitting = false;
        this.closeEditProfileModal();
      },
      error: (error: Error) => {
        console.error('Error updating profile:', error);
        alert('Failed to update profile. Please try again.');
        this.isSubmitting = false;
      }
    });
  }
}