import { Routes } from '@angular/router';
import {HomepageComponent} from "./components/homepage/homepage.component";
import {AddStudentComponent} from "./components/add-student/add-student.component";
import {AddChapterComponent} from "./components/add-chapter/add-chapter.component";


export const routes: Routes = [
  { path: '', component: HomepageComponent },
  {path:'eleve/ajout',component: AddStudentComponent},
  {path:'chapitre/ajout',component: AddChapterComponent},
];
