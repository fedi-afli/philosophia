import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  CheckUsernameResponse,
  CreateStudentRequest,
  Credentials,
  StudentCountResponse,
  StudentResponse
} from "../models/authentification";



@Injectable({
  providedIn: 'root',
})
export class StudentService {
  private readonly baseUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  generateCredentials(fullName: string): Observable<Credentials> {
    return this.http.post<Credentials>(`${this.baseUrl}/generate-credentials`, { fullName ,withCredentials: true });
  }

  checkUsername(username: string): Observable<CheckUsernameResponse> {
    return this.http.get<CheckUsernameResponse>(`${this.baseUrl}/check-username`, {
      params: { username },
    withCredentials: true
    },);
  }

  createStudent(payload: CreateStudentRequest): Observable<StudentResponse> {
    return this.http.post<StudentResponse>(`${this.baseUrl}/students`, payload,{ withCredentials: true });
  }
  getStudentCount(): Observable<StudentCountResponse> {
    return this.http.get<StudentCountResponse>(`${this.baseUrl}/studentCount`, { withCredentials: true });
  }
}
