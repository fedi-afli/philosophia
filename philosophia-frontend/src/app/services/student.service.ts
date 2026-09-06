import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Credentials {
  username: string;
  password: string;
}

export interface CheckUsernameResponse {
  available: boolean;
}

export interface CreateStudentRequest {
  firstName: string;
  lastName: string;
  username: string;
  password: string;
  phone?: string;
  institute?: string;
  sectionId?: number | null;
}

export interface StudentResponse {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
}

@Injectable({
  providedIn: 'root',
})
export class StudentService {
  private readonly baseUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  generateCredentials(fullName: string): Observable<Credentials> {
    return this.http.post<Credentials>(`${this.baseUrl}/generate-credentials`, { fullName });
  }

  checkUsername(username: string): Observable<CheckUsernameResponse> {
    return this.http.get<CheckUsernameResponse>(`${this.baseUrl}/check-username`, {
      params: { username },
    });
  }

  createStudent(payload: CreateStudentRequest): Observable<StudentResponse> {
    return this.http.post<StudentResponse>(`${this.baseUrl}/students`, payload);
  }
}
