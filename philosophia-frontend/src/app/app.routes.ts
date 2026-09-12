import { Routes } from '@angular/router';
import { HomepageComponent } from "./components/homepage/homepage.component";
import { AddStudentComponent } from "./components/add-student/add-student.component";
import { AddChapterComponent } from "./components/add-chapter/add-chapter.component";
import { LoginComponent } from "./components/login/login.component";
import { authGuard } from "./guards/auth.guard";
import {adminGuard} from "./guards/admin.guard";
import {MyProfileComponent} from "./components/my-profile/my-profile.component";
import {UnavailabilityPickerComponent} from "./components/unavailability-picker/unavailability-picker.component";
import {AvailabilityPickerComponent} from "./components/availability-picker/availability-picker.component";
import {ChaptersComponent} from "./components/chapter/chapters.component";
import {StudentsComponent} from "./components/students/students.component";


export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', component: HomepageComponent, canActivate: [authGuard] },
  { path: 'mes-undisponibilites', component: UnavailabilityPickerComponent, canActivate: [authGuard] }, // student
  { path: 'mes-disponibilites', component: AvailabilityPickerComponent, canActivate: [authGuard, adminGuard] }, // admin/teacher
  { path: 'profil', component: MyProfileComponent, canActivate: [authGuard] },
  { path: 'eleve', component: StudentsComponent, canActivate: [authGuard, adminGuard] },
  { path: 'eleve/ajout', component: AddStudentComponent, canActivate: [authGuard, adminGuard] },
  { path: 'chapitre', component: ChaptersComponent, canActivate: [authGuard, adminGuard] },
  { path: 'chapitre/ajout', component: AddChapterComponent, canActivate: [authGuard, adminGuard] },
];
