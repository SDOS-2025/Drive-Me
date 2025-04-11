import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { AuthService } from '../../auth/auth.service';

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
  returnUrl: string = '';

  user = {
    full_name: '',
    email: '',
    phone: '',
    aadhar_card: '',
    password_hash: '',
    email_or_phone: '',
    rememberMe: false
  };

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    // Get return URL from route parameters or default to dashboard
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || 
      (this.userType === 'driver' ? '/driver-dashboard' : '/user-dashboard');
  }

  selectUserType(type: 'driver' | 'regular' | 'admin') {
    this.userType = type;
    // Update returnUrl based on user type
    this.returnUrl = type === 'driver' ? '/driver-dashboard' : 
                     type === 'admin' ? '/admin-dashboard' : '/user-dashboard';
  }

  setFormMode(mode: 'login' | 'signup') {
    this.formMode = mode;
    this.errorMessage = '';
  }

  login() {
    this.loading = true;
    this.errorMessage = '';
    
    const role = this.userType === 'regular' ? 'user' : this.userType;
    
    this.authService.login({
      emailOrPhone: this.user.email_or_phone,
      password: this.user.password_hash,
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

    const signupPayload = {
      fullName: this.user.full_name,
      email: this.user.email,
      phone: this.user.phone,
      aadharCard: this.user.aadhar_card,
      password: this.user.password_hash
    };

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