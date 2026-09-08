import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { filter, map, startWith, switchMap, of } from 'rxjs';
import { AuthService } from "./services/authentification.service";
import { StudentService } from "./services/student.service";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit {
  currentUser$ = this.authService.currentUser$;

  showShell$ = this.router.events.pipe(
    filter((e): e is NavigationEnd => e instanceof NavigationEnd),
    map((e) => e.urlAfterRedirects !== '/login'),
    startWith(this.router.url !== '/login')
  );

  studentCount: number | null = null;

  constructor(
    private router: Router,
    protected authService: AuthService,
    private studentService: StudentService,
  ) {}

  ngOnInit(): void {
    // Refetch whenever the logged-in user changes (login/logout) and is an admin
    this.currentUser$
      .pipe(
        switchMap((user) =>
          user && this.authService.hasRole('ADMIN')
            ? this.studentService.getStudentCount()
            : of(null)
        )
      )
      .subscribe({
        next: (res) => (this.studentCount = res ? res.studentCount : null),
        error: () => (this.studentCount = null),
      });
  }

  ajouterEleve(): void {
    this.router.navigate(['eleve/ajout']);
  }

  programmerChapitre(): void {
    this.router.navigate(['chapitre/ajout']);
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login']),
    });
  }
}
