import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ChapterService } from '../../services/chapter.service';
import { TeachingPlanResponse,  } from '../../models/authentification';
import {UpdateChapterRequest} from "../../models/chapter";

@Component({
  selector: 'app-chapters',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chapters.component.html',
})
export class ChaptersComponent implements OnInit {
  chapters: TeachingPlanResponse[] = [];
  isLoading = true;
  errorMessage = '';

  editingId: number | null = null;
  editForm: UpdateChapterRequest = this.emptyForm();
  isSaving = false;
  editError = '';

  constructor(
    private chapterService: ChapterService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadChapters();
  }

  loadChapters(): void {
    this.isLoading = true;
    this.chapterService.getAllChapters().subscribe({
      next: (res) => {
        this.chapters = res;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = "Impossible de charger les chapitres.";
        this.isLoading = false;
      },
    });
  }

  private emptyForm(): UpdateChapterRequest {
    return {
      chapterName: '',
      type: 'COURS',
      section: 'SCIENTIFIQUE',
      durationWeeks: 1,
      sessionsPerWeek: 1,
      maxStudents: 8,
      startDate: '',
    };
  }

  startEdit(chapter: TeachingPlanResponse): void {
    this.editingId = chapter.id;
    this.editError = '';
    this.editForm = {
      chapterName: chapter.chapterName,
      type: chapter.type as UpdateChapterRequest['type'],
      section: chapter.section as UpdateChapterRequest['section'],
      durationWeeks: chapter.durationWeeks,
      sessionsPerWeek: chapter.sessionsPerWeek,
      maxStudents: chapter.maxStudents,
      startDate: chapter.startDate,
    };
  }

  cancelEdit(): void {
    this.editingId = null;
    this.editError = '';
  }

  get isEditFormValid(): boolean {
    return (
      this.editForm.chapterName.trim().length > 0 &&
      this.editForm.durationWeeks > 0 &&
      this.editForm.sessionsPerWeek > 0 &&
      this.editForm.maxStudents > 0 &&
      this.editForm.startDate.length > 0
    );
  }

  saveEdit(): void {
    if (this.editingId === null || !this.isEditFormValid) return;

    this.isSaving = true;
    this.editError = '';

    this.chapterService.updateChapter(this.editingId, this.editForm).subscribe({
      next: (updated) => {
        const index = this.chapters.findIndex((c) => c.id === updated.id);
        if (index !== -1) this.chapters[index] = updated;
        this.isSaving = false;
        this.editingId = null;
      },
      error: (err) => {
        this.isSaving = false;
        this.editError = err?.error?.message || "Une erreur est survenue lors de la modification.";
      },
    });
  }

  goToAddChapter(): void {
    this.router.navigate(['/chapitre/ajout']);
  }
}
