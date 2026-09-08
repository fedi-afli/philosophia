import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from "../../services/authentification.service";
import { ProfileService } from '../../services/profile.service';
import { StudentResponse, ModifyProfileRequest } from '../../models/authentification';

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

  form: ModifyProfileRequest = { phone: '', institute: '', section: '' };
  private original: ModifyProfileRequest = { phone: '', institute: '', section: '' };

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
      phone: this.profile.phone,
      institute: this.profile.institute,
      section: this.profile.section ?? '',
    };
    this.original = { ...this.form };
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
    this.errorMessage = '';

    const payload: ModifyProfileRequest = {
      phone: this.form.phone !== this.original.phone ? this.form.phone : '',
      institute: this.form.institute !== this.original.institute ? this.form.institute : '',
      section: this.form.section !== this.original.section ? this.form.section : '',
    };

    const hasChanges = Object.values(payload).some((v) => v !== '');
    if (!hasChanges) {
      this.editMode = false;
      return;
    }

    this.isSaving = true;
    this.profileService.updateMyProfile(payload).subscribe({
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
