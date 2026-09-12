package com.philosophia.repository;

import com.philosophia.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {


    // Explicit JPQL — same join path as before (Session -> SessionStudent -> Student -> User),
    // but easier to verify and to add fetch joins to later if N+1 becomes an issue.
    @Query("""
        SELECT DISTINCT s FROM Session s
        JOIN s.students ss
        JOIN ss.student st
        JOIN st.user u
        WHERE u.id = :userId
        ORDER BY s.sessionDate ASC, s.startTime ASC
        """)
    List<Session> findAllForUser(@Param("userId") Long userId);
}