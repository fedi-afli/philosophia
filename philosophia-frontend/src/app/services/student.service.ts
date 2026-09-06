import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {CheckUsernameResponse, CreateStudentRequest, Credentials, StudentResponse} from "../models/authentification";



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
