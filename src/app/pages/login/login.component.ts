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
  user = { email: '', password: '', rememberMe: false };

  login() {
    fetch('http://localhost:8080/users/verify', {
      method: 'POST',
      body: JSON.stringify(this.user),
      headers: {
        'Content-Type': 'application/json'
      }
    });

    console.log('Logging in with:', this.user);
    // Implement login logic here
  }

  signup() {
    fetch('http://localhost:8080/users/add', {
      method: 'POST',
      body: JSON.stringify(this.user),
      headers: {
        'Content-Type': 'application/json'
      }
    });

    console.log('Signing up with:', this.user);
    // Implement signup logic
  }
}
