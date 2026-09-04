import { Component } from '@angular/core';
import { CalendarComponent} from "../calendar/calendar.component";

interface Session {
  day: string;
  title: string;
  topic: string;
  startTime: number; // e.g., 10.5 means 10:30 AM
  duration: number; // Duration in hours
  colorClass: string;
}
@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [
    CalendarComponent
  ],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css'
})
export class HomepageComponent {

  // Calendar headers
  days = ['Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi', 'Dimanche'];

  // Time slots (08:00 to 20:00)
  times = [
    '08:00', '09:00', '10:00', '11:00', '12:00', '13:00',
    '14:00', '15:00', '16:00', '17:00', '18:00', '19:00', '20:00'
  ];

  // Static simulated sessions
  sessions: Session[] = [
    { day: 'Lundi', title: 'Jean M.', topic: 'La Métaphysique, Ch. 3', startTime: 10, duration: 1.5, colorClass: 'bg-indigo-800' },
    { day: 'Mardi', title: 'Marie D.', topic: 'Allégorie de la Caverne', startTime: 13.5, duration: 2, colorClass: 'bg-rose-900' },
    { day: 'Mercredi', title: 'Antoine S.', topic: 'L\'Éthique de Kant', startTime: 8.5, duration: 2, colorClass: 'bg-stone-600' },
    { day: 'Jeudi', title: 'Éléonore L.', topic: 'Intro à l\'Existentialisme', startTime: 16, duration: 2, colorClass: 'bg-teal-900' },
    { day: 'Vendredi', title: 'Sophie P.', topic: 'Le Contrat Social', startTime: 11, duration: 1.5, colorClass: 'bg-amber-700' },
    { day: 'Vendredi', title: 'Élève 4', topic: 'Révision Générale', startTime: 17, duration: 1.5, colorClass: 'bg-rose-900' }
  ];

  // Filter sessions for the current column
  getSessionsForDay(day: string): Session[] {
    return this.sessions.filter(s => s.day === day);
  }

  // Format time (e.g., 13.5 -> "13:30")
  formatTime(time: number): string {
    const hours = Math.floor(time);
    const minutes = (time % 1) * 60;
    return `${hours.toString().padStart(2, '0')}:${minutes === 0 ? '00' : minutes}`;
  }

  // Actions
  ajouterEleve(): void {
    console.log('Ouverture du formulaire: Ajouter un nouvel élève');
  }

  programmerChapitre(): void {
    console.log('Ouverture du formulaire: Programmer un nouveau chapitre');
  }
}
