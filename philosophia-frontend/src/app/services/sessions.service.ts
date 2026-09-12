import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { MySession, ScheduledSession } from "../models/session";

@Injectable({ providedIn: 'root' })
export class SessionsService {
  private readonly baseUrl = `${environment.apiUrl}/sessions`;

  constructor(private http: HttpClient) {}

  getMySessions(): Observable<MySession[]> {
    return this.http.get<MySession[]>(`${this.baseUrl}/me`, { withCredentials: true });
  }

  getAllSessions(): Observable<ScheduledSession[]> {
    return this.http.get<ScheduledSession[]>(this.baseUrl, { withCredentials: true });
  }
}
