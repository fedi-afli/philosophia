import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {TeachingPlanResponse,} from '../models/authentification';
import {CreateChapterRequest, UpdateChapterRequest} from "../models/chapter";
import {GenerateScheduleResponse} from "../models/session";

@Injectable({ providedIn: 'root' })
export class ChapterService {
  private readonly baseUrl = `${environment.apiUrl}/chapters`;

  constructor(private http: HttpClient) {}

  createChapter(payload: CreateChapterRequest): Observable<TeachingPlanResponse> {
    return this.http.post<TeachingPlanResponse>(this.baseUrl, payload, { withCredentials: true });
  }

  getAllChapters(): Observable<TeachingPlanResponse[]> {
    return this.http.get<TeachingPlanResponse[]>(this.baseUrl, { withCredentials: true });
  }

  generateSchedule(teachingPlanId: number): Observable<GenerateScheduleResponse> {
    return this.http.post<GenerateScheduleResponse>(
      `${this.baseUrl}/${teachingPlanId}/schedule`,
      {},
      { withCredentials: true }
    );
  }
  getChapterById(id: number): Observable<TeachingPlanResponse> {
    return this.http.get<TeachingPlanResponse>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }

  updateChapter(id: number, payload: UpdateChapterRequest): Observable<TeachingPlanResponse> {
    return this.http.put<TeachingPlanResponse>(`${this.baseUrl}/${id}`, payload, { withCredentials: true });
  }

}
