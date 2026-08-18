import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginResponse, Role } from '../models/utilisateur.model';

const TOKEN_KEY = 'etatcivil_token';
const ROLE_KEY = 'etatcivil_role';

@Injectable({ providedIn: 'root' })
export class AuthService {
  role = signal<Role | null>(this.readRole());

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/auth/login`, { email, password })
      .pipe(
        tap((res) => {
          localStorage.setItem(TOKEN_KEY, res.token);
          localStorage.setItem(ROLE_KEY, res.role);
          this.role.set(res.role);
        })
      );
  }
  register(nom: string, email: string, password: string): Observable<LoginResponse> {
  return this.http
    .post<LoginResponse>(`${environment.apiUrl}/auth/register`, { nom, email, password })
    .pipe(
      tap((res) => {
        localStorage.setItem(TOKEN_KEY, res.token);
        localStorage.setItem(ROLE_KEY, res.role);
        this.role.set(res.role);
      })
    );
}

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(ROLE_KEY);
    this.role.set(null);
  }

  get token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.token;
  }

  private readRole(): Role | null {
    return (localStorage.getItem(ROLE_KEY) as Role) || null;
  }
}
