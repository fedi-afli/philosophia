import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface PhilosophySession {
  day: 'Lundi' | 'Mardi' | 'Mercredi' | 'Jeudi' | 'Vendredi' | 'Samedi' | 'Dimanche';
  studentName: string;
  chapterTopic: string;
  startTime: number;
  duration: number;
  badgeColor: string;
}

export interface UnavailabilityBlock {
  day: 'Lundi' | 'Mardi' | 'Mercredi' | 'Jeudi' | 'Vendredi' | 'Samedi' | 'Dimanche';
  startTime: number;
  duration: number;
}

const DAY_LABELS: Array<UnavailabilityBlock['day']> = [
  'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi', 'Dimanche',
];

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.css'
})
export class CalendarComponent {
  days: Array<'Lundi' | 'Mardi' | 'Mercredi' | 'Jeudi' | 'Vendredi' | 'Samedi' | 'Dimanche'> = [
    'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi', 'Dimanche'
  ];

  timeSlots = [
    '08:00', '09:00', '10:00', '11:00', '12:00', '13:00',
    '14:00', '15:00', '16:00', '17:00', '18:00', '19:00', '20:00'
  ];

  @Input() sessions: PhilosophySession[] = [

  ];

  // Grey unavailability blocks — set from outside via input, empty by default
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

  private timeStringToDecimal(time: string): number {
    const [h, m] = time.split(':').map(Number);
    return h + m / 60;
  }

  getSessionsForDay(day: string): PhilosophySession[] {
    return this.sessions.filter(s => s.day === day);
  }

  getUnavailabilityForDay(day: string): UnavailabilityBlock[] {
    return this.unavailabilityBlocks.filter(u => u.day === day);
  }

  formatTime(time: number): string {
    const hours = Math.floor(time);
    const minutes = (time % 1) * 60;
    return `${hours.toString().padStart(2, '0')}:${minutes === 0 ? '00' : minutes}`;
  }
}
