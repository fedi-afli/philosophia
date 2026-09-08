import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AvailabilityRangeDto } from '../models/authentification';

@Injectable({ providedIn: 'root' })
export class TeacherAvailabilityService {
  private readonly baseUrl = `${environment.apiUrl}/users/teacher/availability`;

  constructor(private http: HttpClient) {}

  getAvailability(): Observable<AvailabilityRangeDto[]> {
    return this.http.get<AvailabilityRangeDto[]>(this.baseUrl, { withCredentials: true });
  }

  updateAvailability(ranges: AvailabilityRangeDto[]): Observable<AvailabilityRangeDto[]> {
    return this.http.put<AvailabilityRangeDto[]>(this.baseUrl, { ranges }, { withCredentials: true });
  }
}
