import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface PhilosophySession {
  day: 'Lundi' | 'Mardi' | 'Mercredi' | 'Jeudi' | 'Vendredi' | 'Samedi' | 'Dimanche';
  studentName: string;
  chapterTopic: string;
  startTime: number; // 24h format (e.g., 10.5 = 10:30)
  duration: number;  // Hours (e.g., 1.5 = 1h30)
  badgeColor: string;
}

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

  // Static philosophy teaching sessions simulation
  sessions: PhilosophySession[] = [
    {
      day: 'Lundi',
      studentName: 'Jean M.',
      chapterTopic: 'Ch. 3 - La Métaphysique de Descartes',
      startTime: 10,
      duration: 1.5,
      badgeColor: 'bg-indigo-900 border-indigo-700'
    },
    {
      day: 'Mardi',
      studentName: 'Marie D.',
      chapterTopic: 'Ch. 1 - Allégorie de la Caverne (Platon)',
      startTime: 13.5,
      duration: 2,
      badgeColor: 'bg-rose-900 border-rose-700'
    },
    {
      day: 'Mercredi',
      studentName: 'Antoine S.',
      chapterTopic: 'Ch. 5 - L\'Éthique de Kant',
      startTime: 8.5,
      duration: 2,
      badgeColor: 'bg-stone-700 border-stone-600'
    },
    {
      day: 'Jeudi',
      studentName: 'Éléonore L.',
      chapterTopic: 'Ch. 4 - Intro à l\'Existentialisme',
      startTime: 16,
      duration: 2,
      badgeColor: 'bg-teal-900 border-teal-700'
    },
    {
      day: 'Vendredi',
      studentName: 'Sophie P.',
      chapterTopic: 'Ch. 8 - Le Contrat Social (Rousseau)',
      startTime: 11,
      duration: 1.5,
      badgeColor: 'bg-amber-800 border-amber-600'
    },
    {
      day: 'Vendredi',
      studentName: 'Sophie P.',
      chapterTopic: 'Ch. 8 - Le Contrat Social (Rousseau)',
      startTime: 17,
      duration: 1.5,
      badgeColor: 'bg-rose-900 border-rose-700'
    }
  ];

  getSessionsForDay(day: string): PhilosophySession[] {
    return this.sessions.filter(s => s.day === day);
  }

  formatTime(time: number): string {
    const hours = Math.floor(time);
    const minutes = (time % 1) * 60;
    return `${hours.toString().padStart(2, '0')}:${minutes === 0 ? '00' : minutes}`;
  }
}
