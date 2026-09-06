import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, CurrentUser } from '../models/authentification';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  private currentUserSubject = new BehaviorSubject<CurrentUser | null>(this.restoreFromStorage());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials, { withCredentials: true })
      .pipe(
        tap((res) => {
          const user: CurrentUser = { username: res.username, role: res.role, profile: res.profile };
          this.currentUserSubject.next(user);
          sessionStorage.setItem('currentUser', JSON.stringify(user));
        })
      );
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, {}, { withCredentials: true })
      .pipe(
        tap(() => {
          this.currentUserSubject.next(null);
          sessionStorage.removeItem('currentUser');
        })
      );
  }

  // Call this after a successful profile update so the header/session stays in sync
  updateStoredProfile(profile: CurrentUser['profile']): void {
    const current = this.currentUserSubject.value;
    if (!current) return;
    const updated: CurrentUser = { ...current, profile };
    this.currentUserSubject.next(updated);
    sessionStorage.setItem('currentUser', JSON.stringify(updated));
  }

  getCurrentUser(): CurrentUser | null {
    return this.currentUserSubject.value;
  }

  isAuthenticated(): boolean {
    return this.currentUserSubject.value !== null;
  }

  hasRole(role: string): boolean {
    const currentRole = this.currentUserSubject.value?.role;
    return !!currentRole && currentRole.trim().toUpperCase() === role.trim().toUpperCase();
  }

  private restoreFromStorage(): CurrentUser | null {
    const raw = sessionStorage.getItem('currentUser');
    return raw ? JSON.parse(raw) : null;
  }
}
