import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService} from "../../services/authentification.service";
import { ProfileService } from '../../services/profile.service';
import { StudentResponse, UpdateStudentProfileRequest } from '../../models/authentification';

@Component({
  selector: 'app-my-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './my-profile.component.html',
})
export class MyProfileComponent implements OnInit {
  profile: StudentResponse | null = null;
  editMode = false;
  isSaving = false;
  errorMessage = '';

  form: UpdateStudentProfileRequest = { firstName: '', lastName: '', phone: '', institute: '' };

  constructor(
    private authService: AuthService,
    private profileService: ProfileService,
  ) {}

  ngOnInit(): void {
    this.profile = this.authService.getCurrentUser()?.profile ?? null;
    if (this.profile) {
      this.resetForm();
    }
  }

  private resetForm(): void {
    if (!this.profile) return;
    this.form = {
      firstName: this.profile.firstName,
      lastName: this.profile.lastName,
      phone: this.profile.phone,
      institute: this.profile.institute,
    };
  }

  enableEdit(): void {
    this.editMode = true;
  }

  cancelEdit(): void {
    this.resetForm();
    this.editMode = false;
    this.errorMessage = '';
  }

  save(): void {
    this.isSaving = true;
    this.errorMessage = '';

    this.profileService.updateMyProfile(this.form).subscribe({
      next: (updated) => {
        this.profile = updated;
        this.authService.updateStoredProfile(updated);
        this.editMode = false;
        this.isSaving = false;
      },
      error: () => {
        this.errorMessage = "Une erreur est survenue lors de l'enregistrement.";
        this.isSaving = false;
      },
    });
  }
}
