import { Routes } from '@angular/router';
import { HomepageComponent } from "./components/homepage/homepage.component";
import { AddStudentComponent } from "./components/add-student/add-student.component";
import { AddChapterComponent } from "./components/add-chapter/add-chapter.component";
import { LoginComponent } from "./components/login/login.component";
import { authGuard } from "./guards/auth.guard";
import {adminGuard} from "./guards/admin.guard";
import {MyProfileComponent} from "./components/my-profile/my-profile.component";


export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', component: HomepageComponent, canActivate: [authGuard] },
  { path: 'profil', component: MyProfileComponent, canActivate: [authGuard] },
  { path: 'eleve/ajout', component: AddStudentComponent, canActivate: [authGuard, adminGuard] },
  { path: 'chapitre/ajout', component: AddChapterComponent, canActivate: [authGuard, adminGuard] },
];
