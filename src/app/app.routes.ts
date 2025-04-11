import { Routes } from "@angular/router";
import { LandingComponent } from './pages/landing/landing.component';
import { LoginComponent } from './pages/login/login.component'; 
import { DriverDashboardComponent } from './pages/driver-dashboard/driver-dashboard.component';
import { UserDashboardComponent } from './pages/user-dashboard/user-dashboard.component';
import { DriverBookingComponent } from "./pages/driver-booking/driver-booking.component";

const routeConfig: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  {path: 'driver-dashboard', component: DriverDashboardComponent}, 
  {path: 'user-dashboard', component: UserDashboardComponent},
  {path: 'driver-booking', component: DriverBookingComponent},
];

export default routeConfig;
