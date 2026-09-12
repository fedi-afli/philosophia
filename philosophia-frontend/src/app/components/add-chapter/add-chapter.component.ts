import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ChapterService } from '../../services/chapter.service';
import { TeachingPlanResponse,  } from '../../models/authentification';
import {CreateChapterRequest} from "../../models/chapter";
import {GenerateScheduleResponse} from "../../models/session";

@Component({
  selector: 'app-add-chapter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-chapter.component.html',
  styleUrl: './add-chapter.component.css'
})
export class AddChapterComponent {
  form: CreateChapterRequest = {
    chapterName: '',
    type: 'COURS',
    section: 'SCIENTIFIQUE',
    durationWeeks: 1,
    sessionsPerWeek: 1,
    maxStudents: 8,
    startDate: '',
  };

  isSaving = false;
  errorMessage = '';
  saveSucceeded = false;
  createdPlan: TeachingPlanResponse | null = null;

  isScheduling = false;
  scheduleResult: GenerateScheduleResponse | null = null;
  scheduleError = '';

  constructor(
    private chapterService: ChapterService,
    private router: Router,
  ) {}

  get isFormValid(): boolean {
    return (
      this.form.chapterName.trim().length > 0 &&
      this.form.durationWeeks > 0 &&
      this.form.sessionsPerWeek > 0 &&
      this.form.maxStudents > 0 &&
      this.form.startDate.length > 0
    );
  }

  save(): void {
    if (!this.isFormValid) return;

    this.isSaving = true;
    this.errorMessage = '';

    this.chapterService.createChapter(this.form).subscribe({
      next: (res) => {
        this.isSaving = false;
        this.saveSucceeded = true;
        this.createdPlan = res;
      },
      error: (err) => {
        this.isSaving = false;
        this.errorMessage = err?.error?.message || "Une erreur est survenue lors de la création du chapitre.";
      },
    });
  }

  generateSchedule(): void {
    if (!this.createdPlan) return;

    this.isScheduling = true;
    this.scheduleError = '';
    this.scheduleResult = null;

    this.chapterService.generateSchedule(this.createdPlan.id).subscribe({
      next: (res) => {
        this.isScheduling = false;
        this.scheduleResult = res;
      },
      error: (err) => {
        this.isScheduling = false;
        this.scheduleError = err?.error?.message || "Une erreur est survenue lors de la génération du planning.";
      },
    });
  }

  addAnother(): void {
    this.saveSucceeded = false;
    this.createdPlan = null;
    this.scheduleResult = null;
    this.scheduleError = '';
    this.form = {
      chapterName: '',
      type: 'COURS',
      section: 'SCIENTIFIQUE',
      durationWeeks: 1,
      sessionsPerWeek: 1,
      maxStudents: 8,
      startDate: '',
    };
  }

  goHome(): void {
    this.router.navigate(['/']);
  }

  formatTime(time: string): string {
    return time.slice(0, 5); // "14:00:00" -> "14:00"
  }
}
