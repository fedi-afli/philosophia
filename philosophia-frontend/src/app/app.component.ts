import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { filter, map, startWith } from 'rxjs';
import { AuthService} from "./services/authentification.service";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
})
export class AppComponent {
  currentUser$ = this.authService.currentUser$;

  // masque la sidebar/header/footer sur la page de login
  showShell$ = this.router.events.pipe(
    filter((e): e is NavigationEnd => e instanceof NavigationEnd),
    map((e) => e.urlAfterRedirects !== '/login'),
    startWith(this.router.url !== '/login')
  );

  constructor(private router: Router, protected authService: AuthService) {}

  ajouterEleve(): void {
    this.router.navigate(['eleve/ajout']);
  }

  programmerChapitre(): void {
    this.router.navigate(['chapitre/ajout']);
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login']), // même en cas d'erreur réseau, on force la déconnexion locale
    });
  }
}
