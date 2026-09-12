package com.philosophia.services;

import com.philosophia.dto.session.MySessionsResponse;
import com.philosophia.dto.session.ScheduledSessionResponse;
import com.philosophia.dto.session.absence.AttendanceEntry;
import com.philosophia.dto.session.absence.ConfirmAttendanceRequest;
import com.philosophia.dto.session.absence.SessionDetailResponse;
import com.philosophia.dto.session.absence.SessionStudentResponse;
import com.philosophia.enums.AttendanceStatus;
import com.philosophia.exceptions.UserNotFoundException;
import com.philosophia.models.Session;
import com.philosophia.models.SessionStudent;
import com.philosophia.models.Student;
import com.philosophia.repository.SessionRepository;
import com.philosophia.repository.SessionStudentRepository;
import com.philosophia.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SessionsService {

    private final SessionRepository sessionRepository;
    private final SessionStudentRepository sessionStudentRepository;
    private final StudentRepository studentRepository;

    public SessionsService(SessionRepository sessionRepository, SessionStudentRepository sessionStudentRepository, StudentRepository studentRepository) {
        this.sessionRepository = sessionRepository;
        this.sessionStudentRepository = sessionStudentRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Sessions for the current student's own calendar — GET /students/me/sessions.
     * userId here is the User id (matches Authentication.getName() usage elsewhere in the app).
     */
    @Transactional
    public List<MySessionsResponse> getMySessions(Long userId) {
        return sessionRepository
                .findAllForUser(userId)
                .stream()
                .map(this::toMySessionsResponse)
                .toList();
    }

    /**
     * Full session list for admin/teacher views.
     */
    @Transactional
    public List<ScheduledSessionResponse> getAllSessions() {
        return sessionRepository.findAll().stream()
                .sorted(Comparator.comparing(Session::getSessionDate).thenComparing(Session::getStartTime))
                .map(session -> new ScheduledSessionResponse(
                        session.getId(),
                        session.getWeekNumber(),
                        session.getSessionNumber(),
                        session.getSessionDate(),
                        session.getStartTime(),
                        session.getEndTime(),
                        session.getTeachingPlan().getMaxStudents(),
                        session.getStudents().size(),
                        session.getStudents().stream()
                                .map(ss -> ss.getStudent().getFirstName() + " " + ss.getStudent().getLastName())
                                .toList(),
                        session.getTeachingPlan().getChapter().getName()
                ))
                .toList();
    }

    private MySessionsResponse toMySessionsResponse(Session session) {
        String topic = session.getTeachingPlan().getChapter().getName();

        return new MySessionsResponse(
                session.getWeekNumber(),
                session.getSessionNumber(),
                session.getSessionDate(),
                session.getStartTime(),
                session.getEndTime(),
                topic
        );
    }
    @Transactional()
    public SessionDetailResponse getSessionDetail(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new UserNotFoundException(sessionId));

        List<SessionStudentResponse> students = session.getStudents().stream()
                .map(ss -> new SessionStudentResponse(
                        ss.getStudent().getId(),
                        ss.getStudent().getFirstName() + " " + ss.getStudent().getLastName(),
                        ss.getAttendanceStatus().name()
                ))
                .toList();

        return new SessionDetailResponse(
                session.getId(),
                session.getTeachingPlan().getChapter().getName(),
                session.getSessionDate(),
                session.getStartTime(),
                session.getEndTime(),
                students
        );
    }

    @Transactional
    public void confirmAttendance(Long sessionId, ConfirmAttendanceRequest req) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new UserNotFoundException(sessionId));

        Map<Long, Boolean> absentByStudentId = req.attendance().stream()
                .collect(Collectors.toMap(AttendanceEntry::studentId, AttendanceEntry::absent));

        List<SessionStudent> sessionStudents = session.getStudents();
        List<Student> studentsToUpdate = new ArrayList<>();

        for (SessionStudent ss : sessionStudents) {
            Long studentId = ss.getStudent().getId();
            boolean isAbsent = absentByStudentId.getOrDefault(studentId, false);

            ss.setAttendanceStatus(isAbsent ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT);

            if (!isAbsent) {
                Student student = ss.getStudent();
                student.setUnpaidSessionsCount(student.getUnpaidSessionsCount() + 1);
                studentsToUpdate.add(student);
            }
        }

        sessionStudentRepository.saveAll(sessionStudents);
        studentRepository.saveAll(studentsToUpdate);
    }
}