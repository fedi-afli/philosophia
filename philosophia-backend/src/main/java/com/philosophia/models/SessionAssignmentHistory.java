package com.philosophia.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "session_assignment_history")
@Getter
@Setter
@NoArgsConstructor
public class SessionAssignmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_student_id")
    private SessionStudent sessionStudent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**
     * Previous session.
     * NULL when the student is assigned for the first time.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_session_id")
    private Session oldSession;

    /**
     * New session.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_session_id")
    private Session newSession;

    /**
     * User who performed the change.
     * Could be the teacher/admin.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
}
