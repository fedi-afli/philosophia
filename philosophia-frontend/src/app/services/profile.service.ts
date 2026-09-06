import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { StudentResponse, UpdateStudentProfileRequest } from '../models/authentification';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly baseUrl = `${environment.apiUrl}/users/students/me`;

  constructor(private http: HttpClient) {}

  updateMyProfile(payload: UpdateStudentProfileRequest): Observable<StudentResponse> {
    return this.http.put<StudentResponse>(this.baseUrl, payload, { withCredentials: true });
  }
}
