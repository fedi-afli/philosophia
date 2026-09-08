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
  sectionId?: number | null;
}

// Matches backend StudentResponse record exactly
export interface StudentResponse {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  phone: string;
  email: string;
  institute: string;
  section: Section;
  unpaidSession: number;
}

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


export interface StudentCountResponse {
  studentCount: number;
}

export type Section = 'SCIENTIFIQUE' | 'LITTERAIRE';

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
