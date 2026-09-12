import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SessionsService } from '../../services/sessions.service';
import { SessionDetail } from '../../models/session';

@Component({
  selector: 'app-attendance-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './attendance-modal.component.html',
})
export class AttendanceModalComponent implements OnChanges {
  @Input() sessionId: number | null = null;
  @Output() closed = new EventEmitter<void>();
  @Output() confirmed = new EventEmitter<void>();

  detail: SessionDetail | null = null;
  isLoading = true;
  errorMessage = '';
  isSaving = false;
  saveSucceeded = false;

  absentMap: Record<number, boolean> = {};

  constructor(private sessionsService: SessionsService) {}

  ngOnChanges(): void {
    if (this.sessionId === null) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.saveSucceeded = false;
    this.absentMap = {};

    this.sessionsService.getSessionDetail(this.sessionId).subscribe({
      next: (detail) => {
        this.detail = detail;
        for (const s of detail.students) {
          // reflect what's actually saved, not a blind default
          this.absentMap[s.studentId] = s.attendanceStatus === 'ABSENT';
        }
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = "Impossible de charger les élèves de cette séance.";
        this.isLoading = false;
      },
    });
  }

  toggleAbsent(studentId: number): void {
    this.absentMap[studentId] = !this.absentMap[studentId];
  }

  close(): void {
    this.closed.emit();
  }

  confirm(): void {
    if (this.sessionId === null) return;

    this.isSaving = true;
    this.errorMessage = '';

    const attendance = Object.entries(this.absentMap).map(([studentId, absent]) => ({
      studentId: Number(studentId),
      absent,
    }));

    this.sessionsService.confirmAttendance(this.sessionId, { attendance }).subscribe({
      next: () => {
        this.isSaving = false;
        this.saveSucceeded = true;
        this.confirmed.emit(); // let the calendar know, but keep the modal open to show confirmation
      },
      error: () => {
        this.isSaving = false;
        this.errorMessage = "Une erreur est survenue lors de l'enregistrement.";
      },
    });
  }
}
