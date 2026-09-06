import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {AuthService} from "../../services/authentification.service";


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  submit(): void {
    if (!this.username.trim() || !this.password.trim()) {
      this.errorMessage = 'Veuillez remplir tous les champs.';
      return;
    }

    this.errorMessage = '';
    this.isLoading = true;

    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.status === 401
          ? "Nom d'utilisateur ou mot de passe incorrect."
          : 'Erreur de connexion, réessayez.';
      },
    });
  }
}
