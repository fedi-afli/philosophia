import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UnavailabilityService} from "../../services/unavailability-service.service";
import { UnavailabilityRangeDto } from '../../models/authentification';

interface UnavailabilityRange {
  dayOfWeek: number;
  dayLabel: string;
  startTime: string;
  endTime: string;
}

@Component({
  selector: 'app-unavailability-picker',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './unavailability-picker.component.html',
  styleUrl: './unavailability-picker.component.css',
})
export class UnavailabilityPickerComponent implements OnInit {
  days = ['Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi', 'Dimanche'];

  private readonly startHour = 8;
  private readonly endHour = 20;
  slotCount = (this.endHour - this.startHour) * 2;
  slotIndices = Array.from({ length: this.slotCount }, (_, i) => i);

  grid: boolean[][] = this.days.map(() => Array(this.slotCount).fill(false));

  private isPainting = false;
  private paintValue = true;

  ranges: UnavailabilityRange[] = [];
  isSaving = false;
  isLoading = true;
  saveSucceeded = false;
  errorMessage = '';

  constructor(private unavailabilityService: UnavailabilityService) {}

  ngOnInit(): void {
    this.unavailabilityService.getMyUnavailability().subscribe({
      next: (existing) => {
        this.applyExisting(existing);
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = "Impossible de charger vos indisponibilités.";
        this.isLoading = false;
      },
    });
  }

  private applyExisting(existing: UnavailabilityRangeDto[]): void {
    for (const r of existing) {
      const startIdx = this.timeToSlot(r.startTime);
      const endIdx = this.timeToSlot(r.endTime);
      for (let i = startIdx; i < endIdx; i++) {
        if (i >= 0 && i < this.slotCount) {
          this.grid[r.dayOfWeek][i] = true;
        }
      }
    }
    this.recomputeRanges();
  }

  private timeToSlot(time: string): number {
    const [h, m] = time.split(':').map(Number);
    return (h - this.startHour) * 2 + (m >= 30 ? 1 : 0);
  }

  slotToTime(index: number): string {
    const totalMinutes = this.startHour * 60 + index * 30;
    const h = Math.floor(totalMinutes / 60);
    const m = totalMinutes % 60;
    return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}`;
  }

  isHourStart(index: number): boolean {
    return index % 2 === 0;
  }

  onCellMouseDown(dayIndex: number, slotIndex: number, event: MouseEvent): void {
    event.preventDefault();
    this.isPainting = true;
    this.paintValue = !this.grid[dayIndex][slotIndex];
    this.grid[dayIndex][slotIndex] = this.paintValue;
  }

  onCellMouseEnter(dayIndex: number, slotIndex: number): void {
    if (!this.isPainting) return;
    this.grid[dayIndex][slotIndex] = this.paintValue;
  }

  onMouseUp(): void {
    if (!this.isPainting) return;
    this.isPainting = false;
    this.recomputeRanges();
  }

  private recomputeRanges(): void {
    const result: UnavailabilityRange[] = [];

    for (let day = 0; day < this.days.length; day++) {
      let rangeStart: number | null = null;

      for (let slot = 0; slot <= this.slotCount; slot++) {
        const isMarked = slot < this.slotCount && this.grid[day][slot];

        if (isMarked && rangeStart === null) {
          rangeStart = slot;
        } else if (!isMarked && rangeStart !== null) {
          result.push({
            dayOfWeek: day,
            dayLabel: this.days[day],
            startTime: this.slotToTime(rangeStart),
            endTime: this.slotToTime(slot),
          });
          rangeStart = null;
        }
      }
    }

    this.ranges = result;
  }

  removeRange(range: UnavailabilityRange): void {
    const startIdx = this.timeToSlot(range.startTime);
    const endIdx = this.timeToSlot(range.endTime);
    for (let i = startIdx; i < endIdx; i++) {
      this.grid[range.dayOfWeek][i] = false;
    }
    this.recomputeRanges();
  }

  clearAll(): void {
    this.grid = this.days.map(() => Array(this.slotCount).fill(false));
    this.ranges = [];
  }

  save(): void {
    this.isSaving = true;
    this.errorMessage = '';
    this.saveSucceeded = false;

    const payload: UnavailabilityRangeDto[] = this.ranges.map((r) => ({
      dayOfWeek: r.dayOfWeek,
      startTime: r.startTime,
      endTime: r.endTime,
    }));

    this.unavailabilityService.updateMyUnavailability(payload).subscribe({
      next: () => {
        this.isSaving = false;
        this.saveSucceeded = true;
        setTimeout(() => (this.saveSucceeded = false), 2000);
      },
      error: () => {
        this.errorMessage = "Une erreur est survenue lors de l'enregistrement.";
        this.isSaving = false;
      },
    });
  }
}
