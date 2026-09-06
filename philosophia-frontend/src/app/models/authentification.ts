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
  section: string;
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

// Only the fields a student is allowed to self-edit —
// excludes id, username, section, unpaidSession on purpose
export interface UpdateStudentProfileRequest {
  firstName: string;
  lastName: string;
  phone?: string;
  institute?: string;
}
