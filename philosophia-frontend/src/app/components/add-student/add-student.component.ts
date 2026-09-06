import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject, switchMap } from 'rxjs';
import { StudentService, Credentials } from '../../services/student.service';
import {Router} from "@angular/router";

@Component({
  selector: 'app-add-student',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-student.component.html',
})
export class AddStudentComponent {
  @Output() closed = new EventEmitter<void>();
  @Output() created = new EventEmitter<void>();

  fullName = '';
  phone = '';
  institute = '';

  credentials: Credentials | null = null;
  usernameStatus: 'idle' | 'checking' | 'available' | 'taken' = 'idle';
  isGenerating = false;
  isSaving = false;
  copied = false;
  errorMessage = '';
  saveSucceeded = false;
  savedStudentName = '';

  private usernameCheck$ = new Subject<string>();

  constructor(private studentService: StudentService,private router : Router) {
    this.usernameCheck$
      .pipe(
        debounceTime(400),
        distinctUntilChanged(),
        switchMap((username) => this.studentService.checkUsername(username))
      )
      .subscribe({
        next: (res) => (this.usernameStatus = res.available ? 'available' : 'taken'),
        error: () => (this.usernameStatus = 'idle'),
      });
  }

  generate(): void {
    if (!this.fullName.trim()) {
      this.errorMessage = 'Veuillez saisir le nom complet.';
      return;
    }
    this.errorMessage = '';
    this.isGenerating = true;

    this.studentService.generateCredentials(this.fullName).subscribe({
      next: (creds) => {
        this.credentials = creds;
        this.usernameStatus = 'available';
        this.isGenerating = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la génération des identifiants.';
        this.isGenerating = false;
      },
    });
  }

  onUsernameEdited(value: string): void {
    if (!this.credentials) return;
    this.credentials.username = value;
    if (!value.trim()) {
      this.usernameStatus = 'idle';
      return;
    }
    this.usernameStatus = 'checking';
    this.usernameCheck$.next(value.trim());
  }

  regeneratePassword(): void {
    if (!this.credentials) return;
    const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%';
    let pwd = '';
    for (let i = 0; i < 10; i++) {
      pwd += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    this.credentials.password = pwd;
  }

  async copyCredentials(): Promise<void> {
    if (!this.credentials) return;
    const text = `Nom d'utilisateur : ${this.credentials.username}\nMot de passe : ${this.credentials.password}`;
    try {
      await navigator.clipboard.writeText(text);
      this.copied = true;
      setTimeout(() => (this.copied = false), 2000);
    } catch {
      this.errorMessage = 'Impossible de copier automatiquement — copiez manuellement.';
    }
  }

  save(): void {
    if (!this.credentials || this.usernameStatus !== 'available' || this.isSaving) {
      if (this.usernameStatus !== 'available') {
        this.errorMessage = "Le nom d'utilisateur n'est pas disponible.";
      }
      return;
    }
    const [firstName, ...rest] = this.fullName.trim().split(/\s+/);
    const lastName = rest.join(' ') || firstName;

    this.isSaving = true;
    this.errorMessage = '';

    this.studentService
      .createStudent({
        firstName,
        lastName,
        username: this.credentials.username,
        password: this.credentials.password,
        phone: this.phone,
        institute: this.institute,
      })
      .subscribe({
        next: (response) => {
          this.isSaving = false;
          this.saveSucceeded = true;
          this.savedStudentName = `${response.firstName} ${response.lastName}`;
          this.created.emit(); // let parent refresh its list now, modal stays open for confirmation
        },
        error: (err) => {
          this.isSaving = false;
          if (err.status === 409) {
            this.errorMessage = "Ce nom d'utilisateur vient d'être pris, régénérez-le.";
            this.usernameStatus = 'taken';
          } else {
            this.errorMessage = "Erreur lors de la création de l'élève.";
          }
        },
      });
  }

  // Explicit dismissal — only way to close after a successful save
  finish(): void {
    this.router.navigate(['/']);
  }

  close(): void {
    // block accidental backdrop-click dismissal once a save is in flight or just succeeded
    if (this.isSaving) return;
    this.closed.emit();
    this.router.navigate(['/']);
  }

}
