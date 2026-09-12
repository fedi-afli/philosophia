import {UnscheduledStudent} from "./authentification";

export type SessionTypeValue = 'COURS' | 'TP';
export interface ScheduledSession {
  sessionId: number;
  weekNumber: number;
  sessionNumber: number;
  sessionDate: string;
  startTime: string;
  endTime: string;
  capacity: number;
  assignedCount: number;
  assignedStudentNames: string[];
  chapterName: string;
}
export interface GenerateScheduleResponse {
  teachingPlanId: number;
  sessions: ScheduledSession[];
  unscheduledStudents: UnscheduledStudent[];
}
export interface MySession {
  weekNumber: number;
  sessionNumber: number;
  sessionDate: string;
  startTime: string;
  endTime: string;
  topic: string;
}
