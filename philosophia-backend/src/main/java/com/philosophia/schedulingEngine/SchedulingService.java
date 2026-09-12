package com.philosophia.schedulingEngine;
import java.util.HashSet;

import com.philosophia.dto.scheduling.UnscheduledStudentResponse;
import com.philosophia.dto.scheduling.GenerateScheduleResponse;
import com.philosophia.dto.session.ScheduledSessionResponse;
import com.philosophia.enums.SessionStatus;
import com.philosophia.enums.AssignmentType;
import com.philosophia.enums.AttendanceStatus;

import com.philosophia.exceptions.UserNotFoundException;
import com.philosophia.models.*;
import com.philosophia.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SchedulingService {

    private static final int SESSION_DURATION_MINUTES = 120; // fixed 2h session length

    private final TeachingPlanRepository teachingPlanRepository;
    private final StudentRepository studentRepository;
    private final TeacherAvailabilityRepository teacherAvailabilityRepository;
    private final UnavailabilityRepository unavailabilityRepository;
    private final SessionRepository sessionRepository;
    private final SessionStudentRepository sessionStudentRepository;

    public SchedulingService(TeachingPlanRepository teachingPlanRepository, StudentRepository studentRepository,

                             TeacherAvailabilityRepository teacherAvailabilityRepository,
                             UnavailabilityRepository unavailabilityRepository,
                             SessionRepository sessionRepository,
                             SessionStudentRepository sessionStudentRepository) {
        this.teachingPlanRepository = teachingPlanRepository;
        this.studentRepository = studentRepository;

        this.teacherAvailabilityRepository = teacherAvailabilityRepository;
        this.unavailabilityRepository = unavailabilityRepository;
        this.sessionRepository = sessionRepository;
        this.sessionStudentRepository = sessionStudentRepository;
    }

    private record TimeSlot(LocalTime start, LocalTime end) {}

    private static class SlotInstance {
        final int dayOfWeek;
        final LocalDate date;
        final LocalTime start;
        final LocalTime end;
        final int capacity;
        int remaining;
        final List<Student> assigned = new ArrayList<>();

        SlotInstance(int dayOfWeek, LocalDate date, LocalTime start, LocalTime end, int capacity) {
            this.dayOfWeek = dayOfWeek;
            this.date = date;
            this.start = start;
            this.end = end;
            this.capacity = capacity;
            this.remaining = capacity;
        }

        String getSortKey() {
            return date + "T" + start;
        }
    }

    @Transactional
    public GenerateScheduleResponse generateSchedule(Long teachingPlanId) {
        System.out.printf("Generating schedule for teaching plan %d\n", teachingPlanId);
        TeachingPlan plan = teachingPlanRepository.findById(teachingPlanId)
                .orElseThrow(() -> new UserNotFoundException(teachingPlanId));

        List<Student> students = studentRepository.findBySection(plan.getSection());
        System.err.printf("students : "+students);
        System.err.printf("plan : "+plan);
        if (students.isEmpty()) {
            System.err.printf("Generating empty stduent");
            return new GenerateScheduleResponse(teachingPlanId, List.of(), List.of());

        }

        Map<Integer, List<TimeSlot>> teacherSlotsByDay = buildTeacherSlotTemplates();

        List<Long> studentIds = students.stream().map(Student::getId).toList();
        Map<Long, List<StudentUnavailability>> unavailByStudent = unavailabilityRepository
                .findByStudentIdIn(studentIds).stream()
                .collect(Collectors.groupingBy(u -> u.getStudent().getId()));

        LocalDate mondayOfStartWeek = plan.getStartDate().with(DayOfWeek.MONDAY);

        List<ScheduledSessionResponse> allCreatedSessions = new ArrayList<>();
        List<UnscheduledStudentResponse> allUnscheduled = new ArrayList<>();

        for (int week = 0; week < plan.getDurationWeeks(); week++) {
            List<SlotInstance> weekSlots = buildWeekSlots(teacherSlotsByDay, mondayOfStartWeek, week,
                    plan.getStartDate(), plan.getMaxStudents());

            if (weekSlots.isEmpty()) {
                for (Student s : students) {
                    allUnscheduled.add(new UnscheduledStudentResponse(
                            s.getId(), s.getFirstName() + " " + s.getLastName(),
                            week + 1, "Aucun créneau enseignant disponible cette semaine"));
                }
                continue;
            }

            Map<Long, List<SlotInstance>> availableByStudent = new HashMap<>();
            for (Student student : students) {
                List<StudentUnavailability> unavail = unavailByStudent.getOrDefault(student.getId(), List.of());
                List<SlotInstance> compatible = weekSlots.stream()
                        .filter(slot -> isStudentAvailable(slot, unavail))
                        .collect(Collectors.toList());
                availableByStudent.put(student.getId(), compatible);
            }

            List<Student> ordered = new ArrayList<>(students);
            ordered.sort(Comparator.comparingInt(s -> availableByStudent.get(s.getId()).size()));

            int needed = plan.getSessionsPerWeek();

            for (Student student : ordered) {
                List<SlotInstance> candidates = availableByStudent.get(student.getId()).stream()
                        .filter(slot -> slot.remaining > 0)
                        .sorted(Comparator.comparingInt((SlotInstance s) -> s.assigned.size()).reversed())
                        .collect(Collectors.toList());

                List<SlotInstance> chosen = new ArrayList<>();
                Set<LocalDate> chosenDates = new HashSet<>();

                for (SlotInstance slot : candidates) {
                    if (chosen.size() == needed) break;
                    if (slot.remaining <= 0) continue;
                    if (chosenDates.contains(slot.date)) continue; // never two sessions for this student on the same day

                    chosen.add(slot);
                    chosenDates.add(slot.date);
                    slot.remaining--;
                }

                if (chosen.size() < needed) {
                    for (SlotInstance slot : chosen) {
                        slot.remaining++;
                    }
                    allUnscheduled.add(new UnscheduledStudentResponse(
                            student.getId(), student.getFirstName() + " " + student.getLastName(),
                            week + 1, "Pas assez de créneaux compatibles avec ses disponibilités"));
                } else {
                    for (SlotInstance slot : chosen) {
                        slot.assigned.add(student);
                    }
                }
            }

            List<SlotInstance> usedSlots = weekSlots.stream()
                    .filter(s -> !s.assigned.isEmpty())
                    .sorted(Comparator.comparing(SlotInstance::getSortKey))
                    .toList();

            int sessionNumber = 1;
            for (SlotInstance slot : usedSlots) {
                Session session = new Session();
                session.setTeachingPlan(plan);
                session.setWeekNumber(week + 1);
                session.setSessionNumber(sessionNumber++);
                session.setSessionDate(slot.date);
                session.setStartTime(slot.start);
                session.setEndTime(slot.end);
                session.setStatus(SessionStatus.SCHEDULED);
                sessionRepository.save(session);

                List<SessionStudent> sessionStudents = slot.assigned.stream().map(student -> {
                    SessionStudent ss = new SessionStudent();
                    ss.setSession(session);
                    ss.setStudent(student);
                    ss.setAssignmentType(AssignmentType.AUTO);
                    ss.setAttendanceStatus(AttendanceStatus.PENDING);
                    return ss;
                }).toList();
                sessionStudentRepository.saveAll(sessionStudents);

                allCreatedSessions.add(new ScheduledSessionResponse(
                        session.getId(), session.getWeekNumber(), session.getSessionNumber(),
                        session.getSessionDate(), session.getStartTime(), session.getEndTime(),
                        plan.getMaxStudents(), slot.assigned.size(),
                        slot.assigned.stream().map(s -> s.getFirstName() + " " + s.getLastName()).toList(),
                        plan.getChapter().getName()
                ));
            }
        }

        return new GenerateScheduleResponse(teachingPlanId, allCreatedSessions, allUnscheduled);
    }

    private Map<Integer, List<TimeSlot>> buildTeacherSlotTemplates() {
        Map<Integer, List<TimeSlot>> result = new HashMap<>();
        for (TeacherAvailability avail : teacherAvailabilityRepository.findAll()) {
            List<TimeSlot> slots = result.computeIfAbsent(avail.getDayOfWeek(), k -> new ArrayList<>());
            LocalTime cursor = avail.getStartTime();
            while (!cursor.plusMinutes(SESSION_DURATION_MINUTES).isAfter(avail.getEndTime())) {
                LocalTime end = cursor.plusMinutes(SESSION_DURATION_MINUTES);
                slots.add(new TimeSlot(cursor, end));
                cursor = end;
            }
        }
        return result;
    }

    private List<SlotInstance> buildWeekSlots(Map<Integer, List<TimeSlot>> teacherSlotsByDay,
                                              LocalDate mondayOfStartWeek, int week,
                                              LocalDate planStartDate, int maxStudents) {
        List<SlotInstance> result = new ArrayList<>();
        for (Map.Entry<Integer, List<TimeSlot>> entry : teacherSlotsByDay.entrySet()) {
            int day = entry.getKey();
            LocalDate date = mondayOfStartWeek.plusDays((long) week * 7 + day);
            if (date.isBefore(planStartDate)) continue;
            for (TimeSlot slot : entry.getValue()) {
                result.add(new SlotInstance(day, date, slot.start(), slot.end(), maxStudents));
            }
        }
        return result;
    }

    private boolean isStudentAvailable(SlotInstance slot, List<StudentUnavailability> unavailability) {
        for (StudentUnavailability u : unavailability) {
            if (!u.getDayOfWeek().equals(slot.dayOfWeek)) continue;
            boolean overlaps = slot.start.isBefore(u.getEndTime()) && u.getStartTime().isBefore(slot.end);
            if (overlaps) return false;
        }
        return true;
    }
}