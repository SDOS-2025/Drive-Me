import { Routes } from "@angular/router";
import { LandingComponent } from './pages/landing/landing.component';
import { LoginComponent } from './pages/login/login.component'; 
import { DriverDashboardComponent } from './pages/driver-dashboard/driver-dashboard.component';
import { UserDashboardComponent } from './pages/user-dashboard/user-dashboard.component';
import { DriverBookingComponent } from "./pages/driver-booking/driver-booking.component";
import { MyVehiclesComponent } from "./pages/my-vehicles/my-vehicles.component";
import { authGuard } from "./auth/auth.guard";
import { MyBookingsComponent } from "./pages/my-bookings/my-bookings.component";
import { FindDriverComponent } from "./pages/find-driver/find-driver.component";

const routeConfig: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { 
    path: 'driver-dashboard', 
    component: DriverDashboardComponent,
    canActivate: [authGuard],
    data: { requiredRole: 'driver' }
  }, 
  {
    path: 'user-dashboard', 
    component: UserDashboardComponent,
    canActivate: [authGuard],
    data: { requiredRole: 'user' }
  },
  {
    path: 'driver-booking', 
    component: DriverBookingComponent,
    canActivate: [authGuard],
    data: { requiredRole: 'user' }
  },
  {
    path: 'my-bookings',
    component: MyBookingsComponent,
    canActivate: [authGuard],
    data: { requiredRole: 'user' }
  },
  {
    path: 'my-vehicles',
    component: MyVehiclesComponent,
    canActivate: [authGuard],
    data: { requiredRole: 'user' }
  },
  {
    path: 'find-driver',
    component: FindDriverComponent,
    canActivate: [authGuard],
    data: { requiredRole: 'user' }
  },
  // Add a catch-all route to redirect to landing page
  { path: '**', redirectTo: '' }
];

export default routeConfig;