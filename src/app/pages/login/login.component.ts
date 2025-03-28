import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from "../../components/navbar/navbar.component";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, NavbarComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  user = { 
    full_name: '', 
    email: '', 
    phone: '', 
    aadhar_card: '', 
    password_hash: '', 
    email_or_phone: '',
    rememberMe: false 
  };

  login() {
    const loginPayload = {
      email: this.user.email_or_phone.includes('@') ? this.user.email_or_phone : null,
      phone: this.user.email_or_phone.includes('@') ? null : this.user.email_or_phone,
      passwordHash: this.user.password_hash
    };
  
    fetch('http://localhost:8080/users/verify', {
      method: 'POST',
      body: JSON.stringify(loginPayload),
      headers: {
        'Content-Type': 'application/json'
      }
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Login failed');
      }
      return response.text();
    })
    .then(data => console.log('Success:', data))
    .catch(error => console.error('Error:', error));
  }
  
  signup() {
    const signupPayload = {
      fullName: this.user.full_name,
      email: this.user.email,
      phone: this.user.phone,
      aadharCard: this.user.aadhar_card,
      passwordHash: this.user.password_hash
    };

    fetch('http://localhost:8080/users/add', {
      method: 'POST',
      body: JSON.stringify(signupPayload),
      headers: {
        'Content-Type': 'application/json'
      }
    });

    console.log('Signing up with:', this.user);
    // Implement signup logic
  }
}