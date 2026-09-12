import {Section} from "./section";

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
export interface StudentCountResponse {
  studentCount: number;
}
