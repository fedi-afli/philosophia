import {Section} from "./section";
import {SessionTypeValue} from "./session";

export interface UpdateChapterRequest {
  chapterName: string;
  type: SessionTypeValue;
  section: Section;
  durationWeeks: number;
  sessionsPerWeek: number;
  maxStudents: number;
  startDate: string;
}

export interface CreateChapterRequest {
  chapterName: string;
  type: SessionTypeValue;
  section: Section; // reusing the existing 'SCIENTIFIQUE' | 'LITTERAIRE' type
  durationWeeks: number;
  sessionsPerWeek: number;
  maxStudents: number;
  startDate: string; // "YYYY-MM-DD"
}
