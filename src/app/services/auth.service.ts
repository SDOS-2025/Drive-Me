import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, interval, timer } from 'rxjs';
import { Router } from '@angular/router';

export interface User {
  id?: number;
  fullName?: string;
  email?: string;
  phone?: string;
  aadharCard?: string;
  role: 'user' | 'driver' | 'admin';
  token?: string;
  refreshToken?: string;
  licenseNumber?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;
  private refreshTokenTimeout: any;
  private apiUrl = 'https://driveme-app-latest.onrender.com';

  constructor(private router: Router) {
    // Try to load user from localStorage on init
    const savedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<User | null>(savedUser ? JSON.parse(savedUser) : null);
    this.currentUser = this.currentUserSubject.asObservable();

    // Start token refresh if user is logged in
    if (this.currentUserValue) {
      this.startRefreshTokenTimer();
    }
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  public get isAuthenticated(): boolean {
    return !!this.currentUserValue?.token;
  }

  async login(credentials: { emailOrPhone: string, password: string, role: 'user' | 'driver' | 'admin' }): Promise<User> {
    // Determine endpoint based on role
    const endpoint = credentials.role === 'driver' ?
      `${this.apiUrl}/auth/driver/login` :
      credentials.role === 'admin' ?
        `${this.apiUrl}/auth/admin/login` :
        `${this.apiUrl}/auth/user/login`;

    const loginPayload = {
      emailOrPhone: credentials.emailOrPhone,
      password: credentials.password
    };

    return fetch(endpoint, {
      method: 'POST',
      body: JSON.stringify(loginPayload),
      headers: {
        'Content-Type': 'application/json'
      }
    })
    .then(response => {
      if (!response.ok) {
        return response.json().then((err: any) => {
          throw new Error(err.message || 'Login failed');
        });
      } return response.json();
    })
    .then(data => {
      // Create user object from response
      const user: User = {
        role: credentials.role,
        token: data.token,
        refreshToken: data.refreshToken,
        id: data.userId,
        fullName: data.fullName,
      };

      console.log('User data:', data);
      if (credentials.role === 'driver') {
        user.licenseNumber = data.licenseNumber;
        console.log('Driver login successful:', user);
      }

      // Store user in localStorage and update subject
      localStorage.setItem("token", data.token);
      localStorage.setItem("refreshToken", data.refreshToken);
      localStorage.setItem('currentUser', JSON.stringify(user));
      this.currentUserSubject.next(user);

      // Start token refresh timer
      this.startRefreshTokenTimer();

      return user;
    });
  }

  async refreshToken(): Promise<boolean> {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) {
      return Promise.resolve(false);
    }

    return fetch(`${this.apiUrl}/auth/refresh-token`, {
      method: 'POST',
      body: JSON.stringify({ refreshToken }),
      headers: {
        'Content-Type': 'application/json'
      }
    })
    .then(response => {
      if (!response.ok) {
        this.logout();
        return false;
      }
      return response.json();
    })
    .then(data => {
      if (!data) return false;

      // Update stored token
      localStorage.setItem('token', data.token);

      // Update user in localStorage and BehaviorSubject
      const user = this.currentUserValue;
      if (user) {
        user.token = data.token;
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
      }

      // Restart refresh timer
      this.startRefreshTokenTimer();

      return true;
    })
    .catch(() => {
      this.logout();
      return false;
    });
  }

  private startRefreshTokenTimer() {
    // Clear any existing timer
    this.stopRefreshTokenTimer();

    // Set up timer to refresh token 30 seconds before expiry (assuming 30-minute token)
    // Default expiry time is 30 minutes (1800000 ms), refresh 30 seconds before
    const refreshIn = 25 * 60 * 1000; // 25 minutes = 1,500,000 ms

    this.refreshTokenTimeout = setTimeout(() => {
      console.log('Refreshing token...');
      this.refreshToken();
    }, refreshIn);
  }

  private stopRefreshTokenTimer() {
    if (this.refreshTokenTimeout) {
      clearTimeout(this.refreshTokenTimeout);
    }
  }

  async signup(userDetails: any, role: 'user' | 'driver'): Promise<User> {
    const endpoint = role === 'driver' ?
      `${this.apiUrl}/auth/driver/signup` :
      `${this.apiUrl}/auth/user/signup`;

    const signupPayload: {
      fullName: string;
      email: string;
      phone: string;
      aadharCard: string;
      password: string;
      licenseNumber?: string;
    } = {
      fullName: userDetails.fullName,
      email: userDetails.email,
      phone: userDetails.phone,
      aadharCard: userDetails.aadharCard,
      password: userDetails.password
    };

    if (role === 'driver') {
      signupPayload['licenseNumber'] = userDetails.licenseNumber;
    }

    return fetch(endpoint, {
      method: 'POST',
      body: JSON.stringify(signupPayload),
      headers: {
        'Content-Type': 'application/json'
      }
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Signup failed');
      }
      return response.json();
    })
    .then(data => {
      // After signup, you might want to login the user automatically
      // or just return the created user
      return {
        ...data,
        role
      };
    });
  }

  logout(): void {
    // Stop refresh timer
    this.stopRefreshTokenTimer();

    // Remove user from local storage
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }
}
