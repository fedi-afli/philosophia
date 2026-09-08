import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { UnavailabilityRangeDto } from '../models/authentification';

@Injectable({ providedIn: 'root' })
export class UnavailabilityService {
  private readonly baseUrl = `${environment.apiUrl}/users/students/me/unavailability`;

  constructor(private http: HttpClient) {}

  getMyUnavailability(): Observable<UnavailabilityRangeDto[]> {
    return this.http.get<UnavailabilityRangeDto[]>(this.baseUrl, { withCredentials: true });
  }

  updateMyUnavailability(ranges: UnavailabilityRangeDto[]): Observable<UnavailabilityRangeDto[]> {
    return this.http.put<UnavailabilityRangeDto[]>(this.baseUrl, { ranges }, { withCredentials: true });
  }
}
