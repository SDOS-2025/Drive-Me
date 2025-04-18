import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated) {
    // Check if user has at least one of the required roles if specified
    const requiredRoles = route.data['requiredRole'];
    
    if (requiredRoles) {
      // If requiredRoles is an array, check if user role is included
      if (Array.isArray(requiredRoles)) {
        if (requiredRoles.includes(authService.currentUserValue?.role)) {
          return true;
        }
      } 
      // For backward compatibility with single role string
      else if (authService.currentUserValue?.role === requiredRoles) {
        return true;
      }
      // User doesn't have the required role - redirect based on their actual role
      if (authService.currentUserValue?.role === 'driver') {
        router.navigate(['/driver-dashboard']);
      }
      else if (authService.currentUserValue?.role === 'admin') {
        router.navigate(['/admin-dashboard']);
      } 
      else {
        router.navigate(['/user-dashboard']);
      }
      return false;
    }
    return true; // No role requirements or user has the required role
  }

  // Not logged in - redirect to login
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};