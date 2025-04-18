import { bootstrapApplication, provideClientHydration } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { HTTP_INTERCEPTORS, provideHttpClient,  withInterceptorsFromDi } from '@angular/common/http';
import routeConfig from './app/app.routes';
import { AuthInterceptor } from './app/auth/auth.intercepter';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routeConfig),
    provideClientHydration(),
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  ]
})
  .catch((err) => console.error(err));