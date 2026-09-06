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
export interface StudentResponse {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  username: string;
  role: string;
  token: string;
}

export interface CurrentUser {
  username: string;
  role: string;
}
