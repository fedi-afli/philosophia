package com.philosophia.services;

import com.philosophia.dto.session.MySessionsResponse;
import com.philosophia.dto.session.ScheduledSessionResponse;
import com.philosophia.models.Session;
import com.philosophia.repository.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class SessionsService {

    private final SessionRepository sessionRepository;

    public SessionsService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
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
}