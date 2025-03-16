import { Routes } from "@angular/router";
import { LandingComponent } from './pages/landing/landing.component';
import { LoginComponent } from './pages/login/login.component'; 

const routeConfig: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent }, 
];

export default routeConfig;
