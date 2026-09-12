import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { StudentService } from '../../services/student.service';
import {AdminUpdateStudentRequest } from '../../models/authentification';
import {StudentResponse} from "../../models/student";

@Component({
  selector: 'app-students',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './students.component.html',
})
export class StudentsComponent implements OnInit {
  students: StudentResponse[] = [];
  isLoading = true;
  errorMessage = '';

  searchTerm = '';
  sectionFilter: '' | 'SCIENTIFIQUE' | 'LITTERAIRE' = '';
  instituteFilter = '';

  editingId: number | null = null;
  editForm: AdminUpdateStudentRequest = this.emptyForm();
  isSaving = false;
  editError = '';

  constructor(
    private studentService: StudentService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    this.isLoading = true;
    this.studentService.getAllStudents().subscribe({
      next: (res) => {
        this.students = res;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = "Impossible de charger les élèves.";
        this.isLoading = false;
      },
    });
  }

  get uniqueInstitutes(): string[] {
    const set = new Set(this.students.map((s) => s.institute).filter((v) => !!v));
    return Array.from(set).sort();
  }

  get filteredStudents(): StudentResponse[] {
    const term = this.searchTerm.trim().toLowerCase();

    return this.students.filter((s) => {
      const matchesSearch =
        !term ||
        `${s.firstName} ${s.lastName}`.toLowerCase().includes(term) ||
        s.username.toLowerCase().includes(term);

      const matchesSection = !this.sectionFilter || s.section === this.sectionFilter;
      const matchesInstitute = !this.instituteFilter || s.institute === this.instituteFilter;

      return matchesSearch && matchesSection && matchesInstitute;
    });
  }

  private emptyForm(): AdminUpdateStudentRequest {
    return { firstName: '', lastName: '', phone: '', institute: '', section: 'SCIENTIFIQUE' };
  }

  startEdit(student: StudentResponse): void {
    this.editingId = student.id;
    this.editError = '';
    this.editForm = {
      firstName: student.firstName,
      lastName: student.lastName,
      phone: student.phone,
      institute: student.institute,
      section: student.section as AdminUpdateStudentRequest['section'],
    };
  }

  cancelEdit(): void {
    this.editingId = null;
    this.editError = '';
  }

  get isEditFormValid(): boolean {
    return this.editForm.firstName.trim().length > 0 && this.editForm.lastName.trim().length > 0;
  }

  saveEdit(): void {
    if (this.editingId === null || !this.isEditFormValid) return;

    this.isSaving = true;
    this.editError = '';

    this.studentService.adminUpdateStudent(this.editingId, this.editForm).subscribe({
      next: (updated) => {
        const index = this.students.findIndex((s) => s.id === updated.id);
        if (index !== -1) this.students[index] = updated;
        this.isSaving = false;
        this.editingId = null;
      },
      error: (err) => {
        this.isSaving = false;
        this.editError = err?.error?.message || "Une erreur est survenue lors de la modification.";
      },
    });
  }

  goToAddStudent(): void {
    this.router.navigate(['/eleve/ajout']);
  }
}
