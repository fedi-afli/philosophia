import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SessionsService } from "../../services/sessions.service";
import { MySession, ScheduledSession } from "../../models/session";
import { AuthService } from "../../services/authentification.service";
import {AttendanceModalComponent} from "../attendance-modal/attendance-modal.component";

export interface PhilosophySession {
  date: string;
  day: 'Lundi' | 'Mardi' | 'Mercredi' | 'Jeudi' | 'Vendredi' | 'Samedi' | 'Dimanche';
  studentName: string;
  chapterTopic: string;
  startTime: number;
  duration: number;
  badgeColor: string;
  weekNumber?: number;
  sessionNumber?: number;
  sessionId?: number; // present only for admin-loaded sessions — click opens attendance modal
}

export interface UnavailabilityBlock {
  day: 'Lundi' | 'Mardi' | 'Mercredi' | 'Jeudi' | 'Vendredi' | 'Samedi' | 'Dimanche';
  startTime: number;
  duration: number;
}

const DAY_LABELS: Array<UnavailabilityBlock['day']> = [
  'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi', 'Dimanche',
];

const BADGE_PALETTE = [
  'bg-indigo-500', 'bg-emerald-500', 'bg-amber-500', 'bg-rose-500', 'bg-sky-500', 'bg-violet-500',
];

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule, AttendanceModalComponent],
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.css'
})
export class CalendarComponent implements OnInit {
  days = DAY_LABELS;

  timeSlots = [
    '08:00', '09:00', '10:00', '11:00', '12:00', '13:00',
    '14:00', '15:00', '16:00', '17:00', '18:00', '19:00', '20:00'
  ];
  private colorByChapter = new Map<string, string>();

  @Input() sessions: PhilosophySession[] = [];

  isLoadingSessions = true;
  sessionsErrorMessage = '';

  // The Monday of the week currently being viewed
  currentWeekMonday: Date = this.mondayOf(new Date());

  @Input() set unavailability(ranges: { dayOfWeek: number; startTime: string; endTime: string }[]) {
    this.unavailabilityBlocks = ranges.map((r) => {
      const start = this.timeStringToDecimal(r.startTime);
      const end = this.timeStringToDecimal(r.endTime);
      return {
        day: DAY_LABELS[r.dayOfWeek],
        startTime: start,
        duration: end - start,
      };
    });
  }

  unavailabilityBlocks: UnavailabilityBlock[] = [];

  constructor(
    private sessionsService: SessionsService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    if (this.sessions.length > 0) {
      this.isLoadingSessions = false;
      this.currentWeekMonday = this.mondayOf(this.parseIsoDate(this.sessions[0].date));
      return;
    }

    if (this.authService.hasRole('ADMIN')) {
      this.sessionsService.getAllSessions().subscribe({
        next: (sessions) => {
          this.sessions = sessions.map((s) => this.toPhilosophySessionFromAdmin(s));
          this.isLoadingSessions = false;
          this.jumpToEarliestWeek();
        },
        error: () => {
          this.sessionsErrorMessage = 'Impossible de charger les séances.';
          this.isLoadingSessions = false;
        },
      });
      return;
    }

    this.sessionsService.getMySessions().subscribe({
      next: (sessions) => {
        this.sessions = sessions.map((s) => this.toPhilosophySession(s));
        this.isLoadingSessions = false;
        this.jumpToEarliestWeek();
      },
      error: () => {
        this.sessionsErrorMessage = 'Impossible de charger vos séances.';
        this.isLoadingSessions = false;
      },
    });
  }

  private jumpToEarliestWeek(): void {
    if (this.sessions.length === 0) return;
    const sorted = [...this.sessions].sort((a, b) => a.date.localeCompare(b.date));
    this.currentWeekMonday = this.mondayOf(this.parseIsoDate(sorted[0].date));
  }

  private toPhilosophySession(session: MySession): PhilosophySession {
    const start = this.timeStringToDecimal(session.startTime);
    const end = this.timeStringToDecimal(session.endTime);
    const chapterKey = session.topic ?? 'Séance de philosophie';

    return {
      date: session.sessionDate,
      day: this.dayLabelFromDate(session.sessionDate),
      studentName: '',
      chapterTopic: chapterKey,
      startTime: start,
      duration: end - start,
      badgeColor: this.getColorForChapter(chapterKey),
      weekNumber: session.weekNumber,
      sessionNumber: session.sessionNumber,
    };
  }

// remove studentName from toPhilosophySessionFromAdmin, keep only the count
  private toPhilosophySessionFromAdmin(session: ScheduledSession): PhilosophySession {
    const start = this.timeStringToDecimal(session.startTime);
    const end = this.timeStringToDecimal(session.endTime);
    const chapterKey = session.chapterName ?? 'Séance de philosophie';

    return {
      date: session.sessionDate,
      day: this.dayLabelFromDate(session.sessionDate),
      studentName: `${session.assignedCount}/${session.capacity} élève(s)`,
      chapterTopic: chapterKey,
      startTime: start,
      duration: end - start,
      badgeColor: this.getColorForChapter(chapterKey),
      weekNumber: session.weekNumber,
      sessionNumber: session.sessionNumber,
      sessionId: session.sessionId, // new field, admin-only clickable target
    };
  }

  private parseIsoDate(isoDate: string): Date {
    const [year, month, day] = isoDate.split('-').map(Number);
    return new Date(year, month - 1, day);
  }

  private mondayOf(date: Date): Date {
    const d = new Date(date);
    const jsDay = d.getDay(); // 0 = Sunday ... 6 = Saturday
    const mondayOffset = (jsDay + 6) % 7; // 0 = Monday ... 6 = Sunday
    d.setDate(d.getDate() - mondayOffset);
    d.setHours(0, 0, 0, 0);
    return d;
  }

  private dayLabelFromDate(isoDate: string): UnavailabilityBlock['day'] {
    const date = this.parseIsoDate(isoDate);
    const jsDay = date.getDay();
    const mondayFirstIndex = (jsDay + 6) % 7;
    return DAY_LABELS[mondayFirstIndex];
  }

  private toIsoDate(date: Date): string {
    const y = date.getFullYear();
    const m = (date.getMonth() + 1).toString().padStart(2, '0');
    const d = date.getDate().toString().padStart(2, '0');
    return `${y}-${m}-${d}`;
  }

  private timeStringToDecimal(time: string): number {
    const [h, m] = time.split(':').map(Number);
    return h + m / 60;
  }

  // ---- week navigation ----

  goToPreviousWeek(): void {
    const d = new Date(this.currentWeekMonday);
    d.setDate(d.getDate() - 7);
    this.currentWeekMonday = d;
  }

  goToNextWeek(): void {
    const d = new Date(this.currentWeekMonday);
    d.setDate(d.getDate() + 7);
    this.currentWeekMonday = d;
  }

  goToToday(): void {
    this.currentWeekMonday = this.mondayOf(new Date());
  }

  /** The actual calendar date for a given column index (0 = Lundi ... 6 = Dimanche) in the currently viewed week. */
  getDateForColumn(dayIndex: number): Date {
    const d = new Date(this.currentWeekMonday);
    d.setDate(d.getDate() + dayIndex);
    return d;
  }

  formatColumnDate(dayIndex: number): string {
    const d = this.getDateForColumn(dayIndex);
    return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' });
  }

  get weekRangeLabel(): string {
    const start = this.currentWeekMonday;
    const end = this.getDateForColumn(6);
    const startStr = start.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' });
    const endStr = end.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });
    return `${startStr} – ${endStr}`;
  }

  get sessionsInCurrentWeek(): PhilosophySession[] {
    const weekDates = new Set(Array.from({ length: 7 }, (_, i) => this.toIsoDate(this.getDateForColumn(i))));
    return this.sessions.filter(s => weekDates.has(s.date));
  }

  // ---- lookups used by the template ----

  getSessionsForDay(dayIndex: number): PhilosophySession[] {
    const iso = this.toIsoDate(this.getDateForColumn(dayIndex));
    return this.sessions.filter(s => s.date === iso);
  }

  private getColorForChapter(chapterKey: string): string {
    if (!this.colorByChapter.has(chapterKey)) {
      const nextColor = BADGE_PALETTE[this.colorByChapter.size % BADGE_PALETTE.length];
      this.colorByChapter.set(chapterKey, nextColor);
    }
    return this.colorByChapter.get(chapterKey)!;
  }

  getUnavailabilityForDay(day: string): UnavailabilityBlock[] {
    return this.unavailabilityBlocks.filter(u => u.day === day);
  }

  formatTime(time: number): string {
    const hours = Math.floor(time);
    const minutes = (time % 1) * 60;
    return `${hours.toString().padStart(2, '0')}:${minutes === 0 ? '00' : minutes}`;
  }
  selectedSessionId: number | null = null;

  onSessionClick(session: PhilosophySession): void {
    if (session.sessionId === undefined) return; // students don't get the modal
    this.selectedSessionId = session.sessionId;
  }

  onModalClosed(): void {
    this.selectedSessionId = null;
  }

  onAttendanceConfirmed(): void {
    this.selectedSessionId = null;
    // refresh the calendar's assignedCount display isn't strictly necessary here since
    // attendance doesn't change who's assigned — only their status — so no reload needed.
  }
}
