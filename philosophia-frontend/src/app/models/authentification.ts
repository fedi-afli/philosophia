import {Section} from "./section";
import {StudentResponse} from "./student";

export interface Credentials {
  username: string;
  password: string;
}

export interface CheckUsernameResponse {
  available: boolean;
}

export interface CreateStudentRequest {
  firstName: string;
  lastName: string;
  username: string;
  password: string;
  phone?: string;
  institute?: string;
  section:Section;
}

// Matches backend StudentResponse record exactly


export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  username: string;
  role: string;
  token: string;
  profile: StudentResponse | null; // null for ADMIN
}

export interface CurrentUser {
  username: string;
  role: string;
  profile: StudentResponse | null;
}






export interface ModifyProfileRequest {
  phone: string;
  institute: string;
  section: Section | '';
}
export interface UnavailabilityRangeDto {
  dayOfWeek: number;
  startTime: string; // "HH:MM" or "HH:MM:SS" — Java LocalTime parses both
  endTime: string;
}
export interface AvailabilityRangeDto {
  dayOfWeek: number;
  startTime: string;
  endTime: string;
}




export interface TeachingPlanResponse {
  id: number;
  chapterName: string;
  type: string;
  section: string;
  durationWeeks: number;
  sessionsPerWeek: number;
  sessionDurationMinutes: number;
  maxStudents: number;
  startDate: string;
  status: string;
  enrolledStudents: number;
}


export interface UnscheduledStudent {
  studentId: number;
  studentName: string;
  weekNumber: number;
  reason: string;
}




export interface AdminUpdateStudentRequest {
  firstName: string;
  lastName: string;
  phone: string;
  institute: string;
  section: Section;
}
