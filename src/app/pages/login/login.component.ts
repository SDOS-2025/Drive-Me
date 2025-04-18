import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, NavbarComponent],
  templateUrl: './login.component.html',
  providers: [AuthService],
  styleUrl: './login.component.css'
})
export class LoginComponent {
  userType: 'driver' | 'regular' | 'admin' = 'regular';
  formMode: 'login' | 'signup' = 'login';
  errorMessage: string = '';
  loading: boolean = false;
  returnUrl: string = '/user-dashboard';
  originalUrl: string | null = null; // Store the original URL for redirection

  user = {
    fullName: '',
    email: '',
    phone: '',
    aadharCard: '',
    licenseNumber: '',
    emailOrPhone: '',
    password: '',
    passwordLogin: '',
    rememberMe: false,
  }

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    // Get return URL from route parameters or default to dashboard
    this.originalUrl = this.route.snapshot.queryParams['returnUrl'] || null;
    this.updateReturnUrl();
  }

  selectUserType(type: 'driver' | 'regular' | 'admin') {
    this.userType = type;
    // Update returnUrl based on user type

    if (type === 'admin') {
      this.formMode = 'login'; // Admins only have login mode
    }
    this.returnUrl = type === 'driver' ? '/driver-dashboard' : 
                     type === 'admin' ? '/admin-dashboard' : '/user-dashboard';
  }

  setFormMode(mode: 'login' | 'signup') {
    this.formMode = mode;
    this.errorMessage = '';
  }

  updateReturnUrl() {
    // If there's an original returnUrl from query params, use that
    if (this.originalUrl) {
      this.returnUrl = this.originalUrl;
    } else {
      // Otherwise, determine based on user type
      const role = this.userType === 'regular' ? 'user' : this.userType;
      this.returnUrl = role === 'driver' ? '/driver-dashboard' : 
                     role === 'admin' ? '/admin-dashboard' : '/user-dashboard';
    }
  }

  login() {
    this.loading = true;
    this.errorMessage = '';
    
    const role = this.userType === 'regular' ? 'user' : this.userType;
    
    // Update returnUrl based on current role before login
    this.returnUrl = role === 'driver' ? '/driver-dashboard' : 
                   role === 'admin' ? '/admin-dashboard' : '/user-dashboard';
    
    this.authService.login({
      emailOrPhone: this.user.emailOrPhone,
      password: this.user.passwordLogin,
      role: role as any // Cast to expected type
    })
    .then(() => {
      this.router.navigate([this.returnUrl]);
    })
    .catch((error: HttpErrorResponse) => {
      this.errorMessage = error.error?.message || 'Login failed. Please check your credentials.';
      console.error('Login error:', error);
    })
    .finally(() => {
      this.loading = false;
    });
  }

  signup() {
    this.loading = true;
    this.errorMessage = '';

    const signupPayload: {
      fullName: string;
      email: string;
      phone: string;
      aadharCard: string;
      password: string;
      licenseNumber?: string;
    } = {
      fullName: this.user.fullName,
      email: this.user.email,
      phone: this.user.phone,
      aadharCard: this.user.aadharCard,
      password: this.user.password
    };

    if (this.userType === 'driver') {
      signupPayload['licenseNumber'] = this.user.licenseNumber;
    }

    const role = this.userType === 'regular' ? 'user' : 'driver';

    this.authService.signup(signupPayload, role as any)
      .then(() => {
        // After successful signup, switch to login mode
        this.formMode = 'login';
        this.errorMessage = 'Account created successfully! Please login.';
      })
      .catch((error: HttpErrorResponse) => {
        this.errorMessage = 'Signup failed. Please try again.';
        console.error('Signup error:', error);
      })
      .finally(() => {
        this.loading = false;
      });
  }
}