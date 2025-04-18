import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, take, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<any> {
    // Add authorization header with JWT token if available
    const token = localStorage.getItem('token');
    
    if (token) {
      request = this.addToken(request, token);
    }


    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Handle 401 error
          return this.handle401Error(request, next);
        } else {
          // Handle other errors
          return throwError(error);
        }
      })
    );
  }

  private addToken(request: HttpRequest<any>, token: string) {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      return new Observable(observer => {
        this.authService.refreshToken()
          .then(success => {
            this.isRefreshing = false;
            
            if (success) {
              const token = localStorage.getItem('token');
              this.refreshTokenSubject.next(token);
              
              // Retry the request with new token
              const clonedRequest = this.addToken(request, token!);
              observer.next(next.handle(clonedRequest));
            } else {
              // Token refresh failed, redirect to login
              this.authService.logout();
              observer.error(new Error('Session expired. Please log in again.'));
            }
          })
          .catch(() => {
            this.isRefreshing = false;
            this.authService.logout();
            observer.error(new Error('Failed to refresh token.'));
          });
      });
    } else {
      return this.refreshTokenSubject.pipe(
        filter(token => token != null),
        take(1),
        switchMap(token => {
          return next.handle(this.addToken(request, token));
        })
      );
    }
  }
}