import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated) {
    // Check if user has required role if specified
    const requiredRole = route.data['requiredRole'];
    if (requiredRole && authService.currentUserValue?.role !== requiredRole) {
      // Redirect to appropriate dashboard based on role
      if (authService.currentUserValue?.role === 'driver') {
        router.navigate(['/driver-dashboard']);
      } else {
        router.navigate(['/user-dashboard']);
      }
      return false;
    }
    return true;
  }

  // Not logged in - redirect to login
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};