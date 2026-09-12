import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CalendarComponent } from "../calendar/calendar.component";
import { AuthService } from '../../services/authentification.service';
import { UnavailabilityService} from "../../services/unavailability-service.service";
import { UnavailabilityRangeDto } from '../../models/authentification';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [CommonModule, CalendarComponent],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css'
})
export class HomepageComponent implements OnInit {
  unavailability: UnavailabilityRangeDto[] = [];

  constructor(
    private router: Router,
    protected authService: AuthService,
    private unavailabilityService: UnavailabilityService,
  ) {}

  ngOnInit(): void {
    if (this.authService.hasRole('STUDENT')) {
      this.unavailabilityService.getMyUnavailability().subscribe({
        next: (ranges) => (this.unavailability = ranges),
        error: () => (this.unavailability = []),
      });
    }
  }

  GoToEleve(): void {
    this.router.navigate(['eleve']);
  }

 GoToChapitre(): void {
    this.router.navigate(['chapitre']);
  }
}
